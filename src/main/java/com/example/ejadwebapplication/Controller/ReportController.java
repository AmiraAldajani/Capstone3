package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.DTOIN.ReportDTOIn;
import com.example.ejadwebapplication.Service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.status(200).body(reportService.getAllReports());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reportService.getReportById(id));
    }

    // يرجّع البلاغ بعد الحفظ (فيه الـ id والحالة)
    @PostMapping("/add")
    public ResponseEntity<?> addReport(@RequestBody @Valid ReportDTOIn dto) {
        return ResponseEntity.status(200).body(reportService.addReport(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Integer id, @RequestBody @Valid ReportDTOIn dto) {
        return ResponseEntity.status(200).body(reportService.updateReport(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Integer id) {
        reportService.deleteReport(id);
        return ResponseEntity.status(200).body(new ApiResponse("Report deleted successfully"));
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<?> closeReport(@PathVariable Integer id) {
        reportService.closeReport(id);
        return ResponseEntity.status(200).body(new ApiResponse("Report closed successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReportsByUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(reportService.getReportsByUser(userId));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<?> getReportsByStaff(@PathVariable Integer staffId) {
        return ResponseEntity.status(200).body(reportService.getReportsByStaff(staffId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportsByStatus(@PathVariable String status) {
        return ResponseEntity.status(200).body(reportService.getReportsByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getReportsByType(@PathVariable String type) {
        return ResponseEntity.status(200).body(reportService.getReportsByType(type));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<?> getReportsByLocation(@PathVariable Integer locationId) {
        return ResponseEntity.status(200).body(reportService.getReportsByLocation(locationId));
    }
}