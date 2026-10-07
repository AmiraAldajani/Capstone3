package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.DTOIN.LocationDTOIn;
import com.example.ejadwebapplication.Service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllLocations() {
        return ResponseEntity.status(200).body(locationService.getAllLocations());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(locationService.getLocationById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLocation(@RequestBody @Valid LocationDTOIn dto) {
        locationService.addLocation(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Location added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer id, @RequestBody @Valid LocationDTOIn dto) {
        locationService.updateLocation(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Location updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Location deleted successfully"));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getLocationsByCity(@PathVariable String city) {
        return ResponseEntity.status(200).body(locationService.getLocationsByCity(city));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getLocationsByType(@PathVariable String type) {
        return ResponseEntity.status(200).body(locationService.getLocationsByType(type));
    }

    // For older locations (like the DataSeeder ones): gets their coordinates from Google
    @PutMapping("/geocode/{id}")
    public ResponseEntity<?> geocodeLocation(@PathVariable Integer id) {
        locationService.geocodeLocation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Location coordinates updated from Google Maps"));
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyLocations(@RequestParam Double lat, @RequestParam Double lng,
                                                @RequestParam(defaultValue = "5") Double radiusKm) {
        return ResponseEntity.status(200).body(locationService.getNearbyLocations(lat, lng, radiusKm));
    }

    @GetMapping("/reverse-geocode")
    public ResponseEntity<?> reverseGeocode(@RequestParam Double lat, @RequestParam Double lng) {
        return ResponseEntity.status(200).body(locationService.reverseGeocode(lat, lng));
    }
}

    @GetMapping("/statistics/{id}")
    public ResponseEntity<?> getLocationStatistics(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(locationService.getLocationStatistics(id));
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopLocations() {
        return ResponseEntity.status(200).body(locationService.getTopLocations());
    }
}