package com.example.admin_service.service;

import com.example.admin_service.entity.Role;
import com.example.admin_service.entity.RoleRequest;
import com.example.admin_service.entity.User;
import com.example.admin_service.enums.RequestStatus;
import com.example.admin_service.repository.RoleRepository;  // Thêm import
import com.example.admin_service.repository.RoleRequestRepository;
import com.example.admin_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.admin_service.enums.ERole;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleApprovalService {

    @Autowired
    private RoleRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;  // Thêm inject

    // 1. User gửi yêu cầu
    public RoleRequest submitRequest(Long userId, String roleName, String docs) {
        RoleRequest request = new RoleRequest();
        request.setUserId(userId);
        request.setRequestedRoleName(roleName);
        request.setDocumentUrls(docs);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());  // Đảm bảo set
        return requestRepository.save(request);
    }

    // 2. Admin lấy danh sách chờ duyệt
    public List<RoleRequest> getPendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING);
    }

    // 3. Admin DUYỆT yêu cầu
    // 3. Admin DUYỆT yêu cầu (Đã sửa lỗi logic update user_roles)
    @Transactional
    public void approveRequest(Long requestId) {
        // --- BƯỚC 1: Validate Request ---
        RoleRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        // --- BƯỚC 2: Xử lý Logic Role (Fix lỗi tại đây) ---
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Xử lý tên Role để khớp với Database (ví dụ: FARM_MANAGER -> ROLE_FARMMANAGER)
        // Lưu ý: Dựa vào ảnh DB của bạn, tên role viết liền (FARMMANAGER), nên cần remove "_"
        String requestedRoleStr = request.getRequestedRoleName().toUpperCase().replace("_", "");
        String enumName = "ROLE_" + requestedRoleStr;

        // Kiểm tra xem Role mới có tồn tại trong Enum và DB không
        ERole eRole;
        try {
            eRole = ERole.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name in request: " + request.getRequestedRoleName());
        }

        Role newRole = roleRepository.findByName(eRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + enumName));

        // --- QUAN TRỌNG: Thay thế Role cũ bằng Role mới ---

        // Cách cũ của bạn: Chỉ xóa GUEST (Sai nếu user đang là role khác muốn đổi)
        // user.getRoles().remove(guestRole);

        // CÁCH MỚI: Xóa toàn bộ role hiện tại của user để đảm bảo user chỉ có 1 role duy nhất mới cấp
        user.getRoles().clear();

        // Thêm role mới vào
        user.getRoles().add(newRole);

        // Lưu User -> Hibernate sẽ tự động update bảng user_roles (xóa dòng cũ, insert dòng mới)
        userRepository.save(user);

        // --- BƯỚC 3: Cập nhật trạng thái Request ---
        request.setStatus(RequestStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);

        System.out.println("LOG: Đã update user " + user.getId() + " thành role duy nhất: " + enumName);
    }

    // 4. Admin TỪ CHỐI yêu cầu
    public void rejectRequest(Long requestId, String reason) {
        RoleRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        request.setAdminNote(reason);
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }
}