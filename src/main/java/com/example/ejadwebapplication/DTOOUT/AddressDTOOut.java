package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

// نتيجة Geocoding و Reverse Geocoding
@Data
@AllArgsConstructor
public class AddressDTOOut {
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private String city;
}
