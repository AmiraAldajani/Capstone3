package com.example.ejadwebapplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageAnalysisDTO {
    private String title;
    private String description;
    private String color;
    private String brand;
    private String categoryName;

    // نعبيه إحنا بعد رد الـ AI، عشان ينرسل مباشرة في ReportDTOIn
    private Integer categoryId;
}