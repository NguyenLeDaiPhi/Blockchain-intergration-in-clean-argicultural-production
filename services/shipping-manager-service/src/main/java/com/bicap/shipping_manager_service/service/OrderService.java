package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShipmentRepository shipmentRepository;
    private final RestTemplate restTemplate;
    
    @Value("${application.config.trading-order-service-url:http://localhost:8082}")
    private String tradingOrderServiceUrl;

    public List<Map<String, Object>> getConfirmedOrders(String userToken) {
        try {
            // Call Trading Order Service to get all confirmed orders
            String url = tradingOrderServiceUrl + "/api/admin/orders/status/CONFIRMED";
            System.out.println("DEBUG: Calling Trading Order Service URL: " + url);
            System.out.println("DEBUG: User token present: " + (userToken != null && !userToken.isEmpty()));
            
            // Prepare headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            if (userToken != null && !userToken.isEmpty()) {
                headers.set("Authorization", "Bearer " + userToken);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Try to get orders
            ResponseEntity<Object> response;
            try {
                System.out.println("DEBUG: Making request to Trading Order Service...");
                response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
                System.out.println("DEBUG: Response status: " + response.getStatusCode());
                System.out.println("DEBUG: Response body type: " + (response.getBody() != null ? response.getBody().getClass().getName() : "null"));
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                // If 401/403, log and return empty list
                System.err.println("ERROR: Error calling Trading Order Service: " + e.getStatusCode() + " - " + e.getMessage());
                System.err.println("ERROR: Response body: " + e.getResponseBodyAsString());
                if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                    System.err.println("ERROR: Trading Order Service requires authentication with ROLE_SHIPPINGMANAGER or ROLE_ADMIN.");
                    System.err.println("ERROR: Current user may not have the required role.");
                }
                return new ArrayList<>();
            } catch (Exception e) {
                System.err.println("ERROR: Unexpected error calling Trading Order Service: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> orders = convertToOrderList(response.getBody());
                System.out.println("DEBUG: Received " + orders.size() + " orders from Trading Order Service");
                
                // Get order IDs that already have shipments - skip if DB is unavailable
                // TEMPORARY FIX: Skip querying shipments if DB connection fails
                // This allows orders to be displayed even when shipping-db is restarting
                Set<Long> existingOrderIds = new HashSet<>();
                try {
                    // Try to get existing shipments, but don't fail if DB is unavailable
                    existingOrderIds = shipmentRepository.findAll().stream()
                        .map(s -> s.getOrderId())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                    System.out.println("DEBUG: Existing shipment order IDs: " + existingOrderIds);
                } catch (Throwable dbException) {
                    // Catch ALL exceptions including transaction exceptions
                    // Log error but continue - assume no shipments exist if DB is unavailable
                    System.err.println("ERROR: Cannot query shipments from database: " + dbException.getClass().getSimpleName());
                    System.err.println("ERROR: Continuing without filtering by shipments (assuming no shipments exist)");
                    existingOrderIds = new HashSet<>(); // Use empty set - show all CONFIRMED orders
                }
                
                // Filter confirmed orders and exclude those already have shipments
                // Make effectively final for lambda by creating a final reference
                final Set<Long> finalExistingOrderIds = Collections.unmodifiableSet(existingOrderIds);
                List<Map<String, Object>> filteredOrders = orders.stream()
                    .filter(order -> {
                        Object statusObj = order.get("status");
                        String status = statusObj != null ? statusObj.toString().toUpperCase() : "";
                        Long orderId = getOrderId(order);
                        boolean isConfirmed = status.equals("CONFIRMED");
                        boolean notInShipments = orderId != null && !finalExistingOrderIds.contains(orderId);
                        System.out.println("DEBUG: Order ID=" + orderId + ", status=" + status + ", isConfirmed=" + isConfirmed + ", notInShipments=" + notInShipments);
                        return isConfirmed && notInShipments;
                    })
                    .map(this::normalizeOrderData)
                    .collect(Collectors.toList());
                
                System.out.println("DEBUG: Returning " + filteredOrders.size() + " filtered orders");
                // Debug: Print first order details
                if (!filteredOrders.isEmpty()) {
                    System.out.println("DEBUG: First order details: " + filteredOrders.get(0));
                }
                return filteredOrders;
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> normalizeOrderData(Map<String, Object> order) {
        Map<String, Object> normalized = new HashMap<>(order);
        
        // Map orderId to id for frontend compatibility
        if (normalized.containsKey("orderId") && !normalized.containsKey("id")) {
            normalized.put("id", normalized.get("orderId"));
        }
        
        // Ensure common fields exist
        if (!normalized.containsKey("retailerName")) {
            normalized.put("retailerName", normalized.get("retailer") != null ? 
                ((Map<?, ?>) normalized.get("retailer")).get("name") : "N/A");
        }
        if (!normalized.containsKey("productName")) {
            // Try to get product name from items if available
            Object items = normalized.get("items");
            if (items instanceof List && !((List<?>) items).isEmpty()) {
                Object firstItem = ((List<?>) items).get(0);
                if (firstItem instanceof Map) {
                    Object productName = ((Map<?, ?>) firstItem).get("productName");
                    if (productName != null) {
                        normalized.put("productName", productName);
                    }
                }
            }
            if (!normalized.containsKey("productName")) {
                normalized.put("productName", normalized.get("product") != null ? 
                    ((Map<?, ?>) normalized.get("product")).get("name") : "Nông sản");
            }
        }
        
        // Map quantity from items if available
        if (!normalized.containsKey("quantity")) {
            Object items = normalized.get("items");
            if (items instanceof List && !((List<?>) items).isEmpty()) {
                int totalQuantity = 0;
                for (Object item : (List<?>) items) {
                    if (item instanceof Map) {
                        Object qty = ((Map<?, ?>) item).get("quantity");
                        if (qty instanceof Number) {
                            totalQuantity += ((Number) qty).intValue();
                        }
                    }
                }
                normalized.put("quantity", totalQuantity);
            } else {
                normalized.put("quantity", 0);
            }
        }
        
        // Map createdAt to orderDate
        if (normalized.containsKey("createdAt") && !normalized.containsKey("orderDate")) {
            normalized.put("orderDate", normalized.get("createdAt"));
        }
        
        return normalized;
    }
    
    private List<Map<String, Object>> convertToOrderList(Object body) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (body instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) body;
            for (Object item : list) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    result.add(map);
                }
            }
        } else if (body instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) body;
            result.add(map);
        }
        
        return result;
    }
    
    private Long getOrderId(Map<String, Object> order) {
        // Try both "id" and "orderId" fields
        Object id = order.get("orderId");
        if (id == null) {
            id = order.get("id");
        }
        if (id instanceof Number) {
            return ((Number) id).longValue();
        } else if (id != null) {
            try {
                return Long.parseLong(id.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
