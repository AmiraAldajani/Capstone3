package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(@RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(aiService.analyzeImage(image));
    }
}