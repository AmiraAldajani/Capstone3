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
}
