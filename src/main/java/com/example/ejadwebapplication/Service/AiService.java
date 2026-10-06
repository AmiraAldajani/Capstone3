package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Config.AiPrompts;
import com.example.ejadwebapplication.DTO.AiMatchResponse;
import com.example.ejadwebapplication.DTO.ImageAnalysisDTO;
import com.example.ejadwebapplication.DTO.MatchResultDTO;
import com.example.ejadwebapplication.DTO.OpenAiResponse;
import com.example.ejadwebapplication.Model.Category;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestClient openAiRestClient;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;

    // ================= 1) تحليل الصورة =================

    public ImageAnalysisDTO analyzeImage(MultipartFile image) {
        validateImage(image);

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ApiException("No categories found, add categories first");
        }
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        String imageUrl = "data:" + image.getContentType() + ";base64," + toBase64(image);

        List<Map<String, Object>> content = List.of(
                Map.of("type", "text", "text", AiPrompts.imageAnalysis(categoryNames)),
                Map.of("type", "image_url", "image_url", Map.of("url", imageUrl))
        );

        ImageAnalysisDTO result;
        try {
            result = objectMapper.readValue(sendToAi(content), ImageAnalysisDTO.class);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Could not read AI response");
        }

        // نحوّل اسم التصنيف لـ id عشان ينرسل مباشرة في ReportDTOIn
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(result.getCategoryName())) {
                result.setCategoryId(category.getId());
            }
        }
        return result;
    }

    // ================= 2) المطابقة =================

    public List<MatchResultDTO> compareReports(Report report, List<Report> candidates) {
        StringBuilder candidatesText = new StringBuilder();
        for (Report candidate : candidates) {
            candidatesText.append(describeReport(candidate)).append("\n");
        }

        String prompt = AiPrompts.reportMatching(describeReport(report), candidatesText.toString());

        try {
            AiMatchResponse response = objectMapper.readValue(sendToAi(prompt), AiMatchResponse.class);
            if (response.getMatches() == null) {
                return new ArrayList<>();
            }
            return response.getMatches();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Could not read AI response");
        }
    }

    // ================= Helpers =================

    // content ممكن يكون نص (للمطابقة) أو قائمة نص + صورة (لتحليل الصورة)
    private String sendToAi(Object content) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of("role", "user", "content", content))
        );

        OpenAiResponse response = openAiRestClient.post()
                .uri("/chat/completions")
                .body(body)
                .retrieve()
                .body(OpenAiResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new ApiException("AI returned an empty response");
        }
        return response.choices().get(0).message().content();
    }

    private String describeReport(Report report) {
        List<String> locationNames = new ArrayList<>();
        for (Location location : report.getLocations()) {
            locationNames.add(location.getName());
        }
        return "id: " + report.getId()
                + " | type: " + report.getType()
                + " | title: " + report.getTitle()
                + " | description: " + report.getDescription()
                + " | color: " + report.getColor()
                + " | brand: " + (report.getBrand() == null ? "unknown" : report.getBrand())
                + " | date: " + report.getItemDate()
                + " | locations: " + String.join(", ", locationNames);
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ApiException("Image is required");
        }
        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new ApiException("File must be an image");
        }
    }

    private String toBase64(MultipartFile image) {
        try {
            return Base64.getEncoder().encodeToString(image.getBytes());
        } catch (Exception e) {
            throw new ApiException("Could not read the image");
        }
    }
}