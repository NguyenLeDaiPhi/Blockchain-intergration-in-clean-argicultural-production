package com.bicap.farm_management.service;

import com.bicap.farm_management.entity.EnvironmentMetric;
import com.bicap.farm_management.entity.ProductionBatch;
import com.bicap.farm_management.repository.EnvironmentMetricRepository;
import com.bicap.farm_management.repository.ProductionBatchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentMetricService {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentMetricService.class);

    @Autowired
    private EnvironmentMetricRepository metricRepository;
    
    @Autowired
    private ProductionBatchRepository batchRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.api.key}") private String apiKey;
    @Value("${weather.city}") private String city;
    @Value("${weather.url}") private String apiUrl;

    public EnvironmentMetric addMetric(Long batchId, EnvironmentMetric metric) {
        ProductionBatch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy Lô sản xuất ID: " + batchId));
        metric.setProductionBatch(batch);
        metric.setFarmId(batch.getFarm());
        if (metric.getRecordedAt() == null) metric.setRecordedAt(LocalDateTime.now());
        return metricRepository.save(metric);
    }

<<<<<<< HEAD:services/Farm_Management/src/main/java/com/bicap/farm_management/service/EnvironmentMetricService.java
    // === SỬA LẠI HÀM NÀY: Trả về List và lưu 2 chỉ số ===
=======
>>>>>>> 2a820501baad4b338ac3e1eab7efc4d529b6fe3f:services/farm-production-service/src/main/java/com/bicap/farm_management/service/EnvironmentMetricService.java
    public List<EnvironmentMetric> syncWeatherFromApi(Long batchId) {
        try {
            ProductionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Lô sản xuất không tồn tại"));

            String finalUrl = apiUrl.replace("{city}", city).replace("{key}", apiKey);
            String response = restTemplate.getForObject(finalUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            double temp = root.path("main").path("temp").asDouble();
            double humidity = root.path("main").path("humidity").asDouble();
            LocalDateTime now = LocalDateTime.now();

            List<EnvironmentMetric> results = new ArrayList<>();

            // 1. Lưu Nhiệt độ
            EnvironmentMetric t = new EnvironmentMetric();
            t.setProductionBatch(batch);
            t.setFarmId(batch.getFarm());
            t.setMetricType("TEMPERATURE");
            t.setValue(temp);
            t.setUnit("Celsius");
            t.setRecordedAt(now);
            results.add(metricRepository.save(t));

            // 2. Lưu Độ ẩm
            EnvironmentMetric h = new EnvironmentMetric();
            h.setProductionBatch(batch);
            h.setFarmId(batch.getFarm());
            h.setMetricType("HUMIDITY");
            h.setValue(humidity);
            h.setUnit("%");
            h.setRecordedAt(now);
            results.add(metricRepository.save(h));

            return results;

        } catch (Exception e) {
<<<<<<< HEAD:services/Farm_Management/src/main/java/com/bicap/farm_management/service/EnvironmentMetricService.java
            e.printStackTrace(); // In lỗi ra console để debug
=======
            logger.error("Error syncing weather data for batch ID: {}", batchId, e);
>>>>>>> 2a820501baad4b338ac3e1eab7efc4d529b6fe3f:services/farm-production-service/src/main/java/com/bicap/farm_management/service/EnvironmentMetricService.java
            throw new RuntimeException("Lỗi đồng bộ thời tiết: " + e.getMessage());
        }
    }

    public List<EnvironmentMetric> getMetricsByBatch(Long batchId) {
        return metricRepository.findByProductionBatchId(batchId);
    }
}