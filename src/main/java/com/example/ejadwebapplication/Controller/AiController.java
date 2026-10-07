package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Service.AiService;
import com.example.ejadwebapplication.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final ReportService reportService;

    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(@RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(aiService.analyzeImage(image));
    }

    // تحليل الصورة + رفع البلاغ بخطوة وحدة (form-data في Postman)
    // locationIds تنرسل كذا: 1,2 أو نفس المفتاح أكثر من مرة
    @PostMapping("/analyze-and-report")
    public ResponseEntity<?> analyzeAndReport(@RequestParam("image") MultipartFile image,
                                              @RequestParam String type,
                                              @RequestParam(required = false) Integer userId,
                                              @RequestParam(required = false) Integer staffId,
                                              @RequestParam Set<Integer> locationIds,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate itemDate) {
        return ResponseEntity.status(200).body(
                reportService.addReportFromImage(image, type, userId, staffId, locationIds, itemDate));
    }
}