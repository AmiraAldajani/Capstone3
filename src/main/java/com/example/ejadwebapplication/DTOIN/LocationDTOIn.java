package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTOIn {

    @NotEmpty(message = "Location name is required")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotEmpty(message = "City is required")
    @Size(max = 30, message = "City must not exceed 30 characters")
    private String city;

    // String هنا عشان رسالة الخطأ تطلع واضحة، ويتحول enum داخل الـ service
    @NotEmpty(message = "Location type is required")
    @Pattern(regexp = "^(MALL|AIRPORT|METRO|UNIVERSITY|HOSPITAL|HOTEL|STADIUM|OTHER)$",
            message = "Type must be one of: MALL, AIRPORT, METRO, UNIVERSITY, HOSPITAL, HOTEL, STADIUM, OTHER")
    private String type;
}
