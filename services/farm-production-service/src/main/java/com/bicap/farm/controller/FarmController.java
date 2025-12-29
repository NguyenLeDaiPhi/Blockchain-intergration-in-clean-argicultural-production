package com.bicap.farm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bicap.farm.model.EnvironmentMetric;
import com.bicap.farm.model.FarmingLog;
import com.bicap.farm.service.FarmService;

@RestController
@RequestMapping("/api/farm")
@CrossOrigin("*") 
public class FarmController {

    @Autowired
    private FarmService farmService;

    // 1. API: Ghi nhật ký công việc
    // Cách gọi: POST /api/farm/logs?batchId=1&activity=TuoiNuoc&desc=DaTuoiXong
    @GetMapping("/logs")
    public FarmingLog addLog(@RequestParam Long batchId, 
                             @RequestParam String activity, 
                             @RequestParam String desc) {
        return farmService.addLog(batchId, activity, desc);
    }

    // 2. API: Xem danh sách nhật ký theo Lô
    // Cách gọi: GET /api/farm/logs/1
    @GetMapping("/logs/{batchId}")
    public List<FarmingLog> getLogs(@PathVariable Long batchId) {
        return farmService.getLogsByBatch(batchId);
    }

    // 3. API VIP: Kích hoạt lấy thời tiết NGAY LẬP TỨC
    // Cách gọi: POST /api/farm/weather/sync?batchId=1&farmId=1
    @GetMapping("/weather/sync")
    public EnvironmentMetric syncWeather(@RequestParam Long batchId, 
                                         @RequestParam Long farmId) {
        return farmService.syncWeather(batchId, farmId);
    }

    // 4. API: Xem lịch sử thời tiết đã lưu
    // Cách gọi: GET /api/farm/weather/1
    @GetMapping("/weather/{batchId}")
    public List<EnvironmentMetric> getWeather(@PathVariable Long batchId) {
        return farmService.getMetrics(batchId);
    }
}
