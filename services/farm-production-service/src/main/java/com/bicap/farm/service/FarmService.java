package com.bicap.farm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bicap.farm.model.EnvironmentMetric;
import com.bicap.farm.model.FarmingLog;
import com.bicap.farm.repository.EnvMetricRepository;
import com.bicap.farm.repository.FarmingLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FarmService {

    // Logger giúp ghi lại lỗi chuyên nghiệp hơn
    private static final Logger logger = LoggerFactory.getLogger(FarmService.class);
    // Khởi tạo ObjectMapper một lần để tối ưu hiệu năng
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired private EnvMetricRepository envRepo;
    @Autowired private FarmingLogRepository logRepo;
    @Autowired private RestTemplate restTemplate;

    // Lấy cấu hình từ file application.properties
    @Value("${weather.api.key}") private String apiKey;
    @Value("${weather.city}") private String city;
    @Value("${weather.url}") private String apiUrl;

    // 1. Chức năng: Ghi nhật ký canh tác (Bón phân, Tưới nước...)
    public FarmingLog addLog(Long batchId, String activity, String description) {
        FarmingLog log = new FarmingLog();
        log.setBatchId(batchId);
        log.setActivity(activity);
        log.setDescription(description);
        log.setLogDate(LocalDate.now());
        return logRepo.save(log);
    }

    // 2. Chức năng: Xem danh sách nhật ký
    public List<FarmingLog> getLogsByBatch(Long batchId) {
        return logRepo.findByBatchId(batchId);
    }

    // 3. Chức năng VIP: Tự động lấy thời tiết và lưu vào DB
    public EnvironmentMetric syncWeather(Long batchId, Long farmId) {
        try {
            // Tạo đường link API chuẩn
            String finalUrl = apiUrl.replace("{city}", city).replace("{key}", apiKey);
            
            // Gọi lên server OpenWeatherMap để lấy dữ liệu
            String response = restTemplate.getForObject(finalUrl, String.class);

            // Dùng ObjectMapper đã khai báo sẵn
            JsonNode root = mapper.readTree(response);
            double temp = root.path("main").path("temp").asDouble();
            double humidity = root.path("main").path("humidity").asDouble();

            // Lưu dữ liệu vào Database của mình
            EnvironmentMetric metric = new EnvironmentMetric();
            metric.setFarmId(farmId);
            metric.setBatchId(batchId);
            metric.setTemperature(temp);
            metric.setHumidity(humidity);
            metric.setRecordedAt(LocalDateTime.now());

            return envRepo.save(metric);
        } catch (Exception e) {
            logger.error("Lỗi khi đồng bộ thời tiết cho batchId {}: {}", batchId, e.getMessage());
            return null;
        }
    }
    
    // 4. Lấy lịch sử môi trường để vẽ biểu đồ
    public List<EnvironmentMetric> getMetrics(Long batchId) {
        return envRepo.findByBatchIdOrderByRecordedAtDesc(batchId);
    }
}