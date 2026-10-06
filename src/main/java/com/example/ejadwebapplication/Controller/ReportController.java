package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.DTO.ReportDTO;
import com.example.ejadwebapplication.Service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.status(200).body(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reportService.getReportById(id));
    }

    @PostMapping
    public ResponseEntity<?> addReport(@Valid @RequestBody ReportDTO reportDTO) {
        return ResponseEntity.status(200).body(reportService.addReport(reportDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Integer id, @Valid @RequestBody ReportDTO reportDTO) {
        return ResponseEntity.status(200).body(reportService.updateReport(id, reportDTO));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Integer id) {
        reportService.deleteReport(id);
        return ResponseEntity.status(200).body("Report deleted successfully");
    }

}
