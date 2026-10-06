package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTOIn {

    // String مثل LocationDTOIn عشان رسالة الخطأ تكون واضحة
    @NotEmpty(message = "Report type is required")
    @Pattern(regexp = "^(LOST|FOUND)$", message = "Type must be LOST or FOUND")
    private String type;

    @NotEmpty(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotEmpty(message = "Color is required")
    @Size(max = 30, message = "Color must not exceed 30 characters")
    private String color;

    // اختياري: أحياناً الماركة مو واضحة
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @URL(message = "Image URL must be valid")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "Item date is required")
    @PastOrPresent(message = "Item date cannot be in the future")
    private LocalDate itemDate;

    // واحد بالضبط منهم، والتحقق في الـ Service
    private Integer userId;
    private Integer staffId;

    @NotNull(message = "Category id is required")
    private Integer categoryId;

    @NotEmpty(message = "At least one location is required")
    @Size(max = 3, message = "A report can have at most 3 locations")
    private Set<Integer> locationIds;
}