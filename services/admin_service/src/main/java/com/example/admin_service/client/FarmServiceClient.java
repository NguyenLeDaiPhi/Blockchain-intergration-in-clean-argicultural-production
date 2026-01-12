package com.example.admin_service.client;

import com.example.admin_service.dto.FarmResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "farm-service", url = "http://localhost:8081")
public interface FarmServiceClient {

    @GetMapping("/api/farm-features")
    List<FarmResponseDTO> getAllFarms();
}