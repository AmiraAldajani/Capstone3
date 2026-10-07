package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Config.AiPrompts;
import com.example.ejadwebapplication.DTO.AiMatchResponse;
import com.example.ejadwebapplication.DTO.GeminiResponse;
import com.example.ejadwebapplication.DTO.ImageAnalysisDTO;
import com.example.ejadwebapplication.DTO.MatchResultDTO;
import com.example.ejadwebapplication.DTO.OpenAiResponse;
import com.example.ejadwebapplication.Model.Category;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    // اسم الحقل لازم يطابق اسم الـ Bean عشان Spring يعرف أي RestClient يحقن
    private final RestClient openAiRestClient;   // للمطابقة
    private final RestClient geminiRestClient;   // لتحليل الصورة
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;

    // مو final عشان ما يدخل في الـ constructor حق Lombok، Spring يعبيه بعدين
    @Value("${ai.model}")
    private String geminiModel;

    // ================= 1) تحليل الصورة (Gemini) =================

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

        String prompt = AiPrompts.imageAnalysis(categoryNames);

        ImageAnalysisDTO result;
        try {
            result = objectMapper.readValue(sendImageToGemini(prompt, image), ImageAnalysisDTO.class);
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

    // ================= 2) المطابقة (OpenAI) =================

    public List<MatchResultDTO> compareReports(Report report, List<Report> candidates) {
        StringBuilder candidatesText = new StringBuilder();
        for (Report candidate : candidates) {
            candidatesText.append(describeReport(candidate)).append("\n");
        }

        String prompt = AiPrompts.reportMatching(describeReport(report), candidatesText.toString());

        try {
            AiMatchResponse response = objectMapper.readValue(sendToOpenAi(prompt), AiMatchResponse.class);
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

    // Gemini: النص والصورة يروحون كـ parts، والصورة base64 خام (بدون data:...;base64,)
    private String sendImageToGemini(String prompt, MultipartFile image) {
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> imagePart = Map.of("inline_data", Map.of(
                "mime_type", image.getContentType(),
                "data", toBase64(image)
        ));

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("role", "user", "parts", List.of(textPart, imagePart))),
                // بديل response_format حق OpenAI: يجبره يرجع JSON
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        GeminiResponse response;
        try {
            response = geminiRestClient.post()
                    .uri("/models/{model}:generateContent", geminiModel)
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientResponseException e) {
            throw new ApiException("Gemini request failed: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            // timeout أو مشكلة اتصال
            throw new ApiException("AI service is not responding, please try again later");
        }

        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new ApiException("AI returned an empty response");
        }
        GeminiResponse.Content content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            throw new ApiException("AI returned an empty response");
        }

        StringBuilder text = new StringBuilder();
        for (GeminiResponse.Part part : content.parts()) {
            if (part.text() != null) {
                text.append(part.text());
            }
        }
        return text.toString();
    }

    // OpenAI: صار للمطابقة بس، فالـ content نص فقط
    private String sendToOpenAi(String prompt) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of("role", "user", "content", prompt))
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