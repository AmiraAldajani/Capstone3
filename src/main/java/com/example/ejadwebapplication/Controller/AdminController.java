package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.DTOIN.AccountDTOIn;
import com.example.ejadwebapplication.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.status(200).body(adminService.getAllAdmins());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(adminService.getAdminById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody @Valid AccountDTOIn dto) {
        adminService.addAdmin(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Admin added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody @Valid AccountDTOIn dto) {
        adminService.updateAdmin(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Admin updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.status(200).body(new ApiResponse("Admin deleted successfully"));
    }

    @PutMapping("/{adminId}/verify-staff/{staffId}")
    public ResponseEntity<?> verifyStaff(@PathVariable Integer adminId, @PathVariable Integer staffId) {
        adminService.verifyStaff(adminId, staffId);
        return ResponseEntity.status(200).body(new ApiResponse("Staff verified successfully"));
    }

    // ================= Extra =================

    @PutMapping("/{adminId}/unverify-staff/{staffId}")
    public ResponseEntity<?> unverifyStaff(@PathVariable Integer adminId, @PathVariable Integer staffId) {
        adminService.unverifyStaff(adminId, staffId);
        return ResponseEntity.status(200).body(new ApiResponse("Staff verification removed successfully"));
    }

    @GetMapping("/{adminId}/statistics")
    public ResponseEntity<?> getStatistics(@PathVariable Integer adminId) {
        return ResponseEntity.status(200).body(adminService.getStatistics(adminId));
    }

    @PutMapping("/{adminId}/close-old-reports/{days}")
    public ResponseEntity<?> closeOldReports(@PathVariable Integer adminId, @PathVariable Integer days) {
        Integer count = adminService.closeOldReports(adminId, days);
        return ResponseEntity.status(200).body(new ApiResponse(count + " old reports closed successfully"));
    }

    @GetMapping("/{adminId}/success-rate")
    public ResponseEntity<?> getSuccessRate(@PathVariable Integer adminId) {
        return ResponseEntity.status(200).body(adminService.getSuccessRate(adminId));
    }

    @PutMapping("/{adminId}/verify-all/{locationId}")
    public ResponseEntity<?> verifyAllStaffAtLocation(@PathVariable Integer adminId, @PathVariable Integer locationId) {
        Integer count = adminService.verifyAllStaffAtLocation(adminId, locationId);
        return ResponseEntity.status(200).body(new ApiResponse(count + " staff verified successfully"));
    }
}