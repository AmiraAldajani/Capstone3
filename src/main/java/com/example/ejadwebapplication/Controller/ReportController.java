package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.DTOIN.ReportDTOIn;
import com.example.ejadwebapplication.Service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    // ================= Extra =================

    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> searchReports(@PathVariable String keyword) {
        return ResponseEntity.status(200).body(reportService.searchReports(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getReportsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.status(200).body(reportService.getReportsByCategory(categoryId));
    }

    // التاريخ بصيغة yyyy-MM-dd، مثال: /date-range/2026-01-01/2026-01-31
    @GetMapping("/date-range/{from}/{to}")
    public ResponseEntity<?> getReportsByDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.status(200).body(reportService.getReportsByDateRange(from, to));
    }

    @PutMapping("/reopen/{id}")
    public ResponseEntity<?> reopenReport(@PathVariable Integer id) {
        reportService.reopenReport(id);
        return ResponseEntity.status(200).body(new ApiResponse("Report reopened successfully"));
    }

    @GetMapping("/staff/{staffId}/location-open")
    public ResponseEntity<?> getOpenReportsAtStaffLocation(@PathVariable Integer staffId) {
        return ResponseEntity.status(200).body(reportService.getOpenReportsAtStaffLocation(staffId));
    }

    // ================= Extra 2 =================

    @GetMapping("/similar/{id}")
    public ResponseEntity<?> getSimilarReports(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reportService.getSimilarReports(id));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getReportsByCity(@PathVariable String city) {
        return ResponseEntity.status(200).body(reportService.getReportsByCity(city));
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<?> getRecentReports(@PathVariable Integer days) {
        return ResponseEntity.status(200).body(reportService.getRecentReports(days));
    }

    @GetMapping("/stale/{days}")
    public ResponseEntity<?> getStaleReports(@PathVariable Integer days) {
        return ResponseEntity.status(200).body(reportService.getStaleReports(days));
    }

    @GetMapping("/user/{userId}/open")
    public ResponseEntity<?> getOpenReportsByUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(reportService.getOpenReportsByUser(userId));
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<?> getReportsByColor(@PathVariable String color) {
        return ResponseEntity.status(200).body(reportService.getReportsByColor(color));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<?> getReportsByBrand(@PathVariable String brand) {
        return ResponseEntity.status(200).body(reportService.getReportsByBrand(brand));
    }

    @GetMapping("/nearby-found/{reportId}")
    public ResponseEntity<?> getNearbyFoundReports(@PathVariable Integer reportId,
                                                   @RequestParam(defaultValue = "5") Double radiusKm) {
        return ResponseEntity.status(200).body(reportService.getNearbyFoundReports(reportId, radiusKm));
    }
}