package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTOIn {

    @NotEmpty(message = "Category name is required")
    @Size(max = 30, message = "Category name must not exceed 30 characters")
    private String name;

    @NotEmpty(message = "Category description is required")
    @Size(max = 200, message = "Category description must not exceed 200 characters")
    private String description;
}