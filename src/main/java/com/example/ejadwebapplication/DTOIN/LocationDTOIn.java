package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.*;
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

    // Optional: if not sent, we get them from Google Maps
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;
}
