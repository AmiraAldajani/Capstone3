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

    public static String reportMatching(String newReport, String candidates) {
        return """
                You are a matching assistant for a lost and found platform in Saudi Arabia.
                Compare the NEW report with each CANDIDATE report and estimate how likely
                they describe the same physical item.
                Consider title, description, color, brand, date and location.
                Reports may be written in Arabic or English.

                NEW report:
                %s

                CANDIDATE reports:
                %s

                Respond ONLY with a JSON object in this exact format:
                {"matches": [{"reportId": <candidate id>, "score": <number from 0 to 100>, "reason": "<short reason in Arabic, max 200 characters>"}]}
                Include every candidate exactly once.
                """.formatted(newReport, candidates);
    }
}