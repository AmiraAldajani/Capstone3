package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

// المكان + بعده عن المستخدم بالكيلو
@Data
@AllArgsConstructor
public class NearbyLocationDTOOut {
    private Double distanceKm;
    private LocationDTOOut location;
}
