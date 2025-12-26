package com.bicap.farm_production.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bicap.farm_production.entity.FarmingProcess;
import com.bicap.farm_production.entity.FarmingSeason;
import com.bicap.farm_production.service.FarmingProcessService;
import com.bicap.farm_production.service.FarmingSeasonService;

@RestController
@RequestMapping("/api/seasons")
public class FarmingSeasonController {

    @Autowired
    private FarmingSeasonService seasonService;

    @Autowired
    private FarmingProcessService processService;

    // 1. Tạo mùa vụ: Lấy ID người dùng từ Header do Kong gửi xuống
    @PostMapping
    public ResponseEntity<FarmingSeason> createSeason(
            @RequestBody FarmingSeason season,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        Long farmId = parseUserId(userIdHeader);
        season.setFarmId(farmId);
        
        return ResponseEntity.ok(seasonService.createSeason(season));
    }

    // 2. Lấy danh sách mùa vụ (Của riêng Farm đó)
    @GetMapping
    public ResponseEntity<List<FarmingSeason>> getAllSeasons(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        // Nếu muốn lọc theo user đang đăng nhập
        // Long farmId = parseUserId(userIdHeader);
        // return ResponseEntity.ok(seasonService.getAllSeasonsByFarmId(farmId));
        
        // Hiện tại tạm thời lấy hết
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @PostMapping("/{seasonId}/processes")
    public ResponseEntity<FarmingProcess> addProcess(
            @PathVariable Long seasonId,
            @RequestBody FarmingProcess process) {
        // Có thể thêm kiểm tra: seasonId này có thuộc về user đang đăng nhập không?
        return ResponseEntity.ok(processService.addProcess(seasonId, process));
    }

    @GetMapping("/{seasonId}/processes")
    public ResponseEntity<List<FarmingProcess>> getProcesses(@PathVariable Long seasonId) {
        return ResponseEntity.ok(processService.getProcessesBySeason(seasonId));
    }

    // Hàm helper để xử lý Header
    private Long parseUserId(String userIdHeader) {
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                System.err.println("Invalid User ID header: " + userIdHeader);
            }
        }
        // Fallback: Trả về ID mặc định (1) để test local khi không chạy qua Kong
        // Khi lên Production nên throw Exception: throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return 1L; 
    }
}