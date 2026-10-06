package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReportDTOOut {
    private Integer id;
    private String type;
    private String title;
    private String description;
    private String color;
    private String brand;
    private String imageUrl;
    private LocalDate itemDate;
    private String status;
    private LocalDateTime createdAt;

    // بدل User/Staff كامل (عشان ما يطلع الـ password)
    private Integer userId;
    private Integer staffId;
    private String reporterName;

    private Integer categoryId;
    private String categoryName;

    private List<LocationDTOOut> locations;
}