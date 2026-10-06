package com.example.ejadwebapplication.Service;

import com.google.genai.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class GeminiService {

        @Value("${gemini.api.key}")
        private String apiKey;

        public String generateText(String prompt) {

            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            return client.models.generateContent(
                    "gemini-3.8-flash",
                    prompt,
                    null
            ).text();
        }
}
