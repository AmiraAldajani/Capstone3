package com.example.ejadwebapplication.Config;

import java.util.List;

public class AiPrompts {

    public static String imageAnalysis(List<String> categoryNames) {
        return """
                You are an assistant for a lost and found platform in Saudi Arabia.
                Analyze the item in the image and respond ONLY with a JSON object with these keys:
                - "title": short item name (max 50 characters)
                - "description": clear description of the item (max 200 characters)
                - "color": main color
                - "brand": brand name if visible, otherwise null
                - "categoryName": must be EXACTLY one of these values: %s
                Write title, description and color in Arabic.
                Do not mention any personal information visible in the image.
                """.formatted(String.join(", ", categoryNames));
    }
}