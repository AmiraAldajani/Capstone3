package com.example.ejadwebapplication.DTO;


import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    @NotNull
    private ReportType type;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String color;

    @NotBlank
    private String brand;


    private String imageUrl;

    @NotNull
    private LocalDate itemDate;

    private User user;

    private Category category;

    private Set<Location> locations;
}
