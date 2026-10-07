package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.DTOIN.StaffDTOIn;
import com.example.ejadwebapplication.Service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.status(200).body(staffService.getAllStaff());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(staffService.getStaffById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStaff(@RequestBody @Valid StaffDTOIn dto) {
        staffService.addStaff(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Staff added successfully, waiting for admin verification"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @RequestBody @Valid StaffDTOIn dto) {
        staffService.updateStaff(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Staff updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.status(200).body(new ApiResponse("Staff deleted successfully"));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<?> getStaffByLocation(@PathVariable Integer locationId) {
        return ResponseEntity.status(200).body(staffService.getStaffByLocation(locationId));
    }

    @GetMapping("/unverified")
    public ResponseEntity<?> getUnverifiedStaff() {
        return ResponseEntity.status(200).body(staffService.getUnverifiedStaff());
    }

    // ================= Extra =================

    @PutMapping("/{staffId}/transfer/{locationId}")
    public ResponseEntity<?> transferStaff(@PathVariable Integer staffId, @PathVariable Integer locationId) {
        staffService.transferStaff(staffId, locationId);
        return ResponseEntity.status(200).body(new ApiResponse("Staff transferred successfully, waiting for admin verification"));
    }

    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedStaff() {
        return ResponseEntity.status(200).body(staffService.getVerifiedStaff());
    }
}