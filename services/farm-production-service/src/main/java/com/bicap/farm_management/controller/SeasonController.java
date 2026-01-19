package com.bicap.farm_management.controller;

import com.bicap.farm_management.entity.ExportBatch;
import com.bicap.farm_management.entity.FarmingProcess;
import com.bicap.farm_management.entity.ProductionBatch;
import com.bicap.farm_management.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi
public class SeasonController {

    @Autowired
    private SeasonService seasonService;

    // 1. Tạo mùa vụ mới
    @PostMapping
    public ResponseEntity<ProductionBatch> createSeason(@RequestBody ProductionBatch batch) {
        return ResponseEntity.ok(seasonService.createSeason(batch));
    }

    // 2. Coi chi tiết mùa vụ
    @GetMapping("/{id}")
    public ResponseEntity<ProductionBatch> getSeasonDetails(@PathVariable Long id) {
        return ResponseEntity.ok(seasonService.getSeasonDetails(id));
    }

    // 3. Coi tiến trình (Nhật ký)
    @GetMapping("/{id}/processes")
    public ResponseEntity<List<FarmingProcess>> getProcesses(@PathVariable Long id) {
        return ResponseEntity.ok(seasonService.getSeasonProcesses(id));
    }

    // 4. Update tiến trình mùa vụ
    @PostMapping("/{id}/processes")
    public ResponseEntity<FarmingProcess> addProcess(@PathVariable Long id, @RequestBody FarmingProcess process) {
        return ResponseEntity.ok(seasonService.addProcess(id, process));
    }

    // 5. Xuất tiến trình & Tạo QR
    @PostMapping("/{id}/export")
    public ResponseEntity<ExportBatch> exportSeason(@PathVariable Long id, @RequestBody ExportBatch exportInfo) {
        return ResponseEntity.ok(seasonService.exportSeason(id, exportInfo));
    }
}