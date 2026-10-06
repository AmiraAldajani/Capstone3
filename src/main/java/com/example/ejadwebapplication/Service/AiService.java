package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Config.AiPrompts;
import com.example.ejadwebapplication.DTO.ImageAnalysisDTO;
import com.example.ejadwebapplication.DTO.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestClient openAiRestClient;
    private final ObjectMapper objectMapper;

    public ImageAnalysisDTO analyzeImage(MultipartFile image) {
        validateImage(image);

        // بعد ما يرفع Member 2 الـ Category: categoryRepository.findAll() ونطلع الأسماء
        List<String> categoryNames = List.of("Electronics", "Wallets", "Bags", "Keys", "Documents", "Clothes", "Other");

        String imageUrl = "data:" + image.getContentType() + ";base64," + toBase64(image);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", List.of(
                                Map.of("type", "text", "text", AiPrompts.imageAnalysis(categoryNames)),
                                Map.of("type", "image_url", "image_url", Map.of("url", imageUrl))
                        )
                ))
        );

        OpenAiResponse response = openAiRestClient.post()
                .uri("/chat/completions")
                .body(body)
                .retrieve()
                .body(OpenAiResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new ApiException("AI returned an empty response");
        }

        String content = response.choices().get(0).message().content();

        try {
            return objectMapper.readValue(content, ImageAnalysisDTO.class);
        } catch (Exception e) {
            throw new ApiException("Could not read AI response");
        }
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