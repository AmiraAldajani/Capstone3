package com.example.ejadwebapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageAnalysisDTO {
    private String title;
    private String description;
    private String color;
    private String brand;
    private String categoryName;
}