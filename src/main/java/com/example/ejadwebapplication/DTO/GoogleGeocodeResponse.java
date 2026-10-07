package com.example.ejadwebapplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// شكل رد Geocoding API: results[0].geometry.location.lat/lng
// قوقل يستخدم snake_case، فنحتاج @JsonProperty للأسماء اللي فيها _
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleGeocodeResponse(
        String status,
        @JsonProperty("error_message") String errorMessage,
        List<Result> results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            @JsonProperty("formatted_address") String formattedAddress,
            Geometry geometry,
            @JsonProperty("address_components") List<AddressComponent> addressComponents) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(LatLng location) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LatLng(Double lat, Double lng) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddressComponent(
            @JsonProperty("long_name") String longName,
            List<String> types) {
    }
}
