package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTO.GoogleGeocodeResponse;
import com.example.ejadwebapplication.DTOOUT.AddressDTOOut;
import com.example.ejadwebapplication.Model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MAX_RADIUS_KM = 50.0;

    // نفس فكرة AiService: اسم الحقل لازم يطابق اسم الـ Bean
    private final RestClient googleMapsRestClient;

    // مو final عشان ما يدخل في الـ constructor حق Lombok
    @Value("${google.maps.api.key}")
    private String apiKey;

    // ================= 1) Geocoding: اسم المكان -> إحداثيات =================

    public AddressDTOOut geocode(String address) {
        // components=country:SA يحصر البحث داخل السعودية
        GoogleGeocodeResponse response = sendRequest(
                "/geocode/json?address={address}&components=country:SA&language=en&key={key}",
                address, apiKey);
        return toAddressDTO(response.results().get(0));
    }

    // ================= 2) Reverse Geocoding: إحداثيات -> عنوان ومدينة =================

    public AddressDTOOut reverseGeocode(Double lat, Double lng) {
        validateCoordinates(lat, lng);
        GoogleGeocodeResponse response = sendRequest(
                "/geocode/json?latlng={lat},{lng}&language=en&key={key}",
                lat, lng, apiKey);
        return toAddressDTO(response.results().get(0));
    }

    // ================= بدون API (ما يكلف أي طلب) =================

    // معادلة Haversine: المسافة بالكيلومتر بين نقطتين على سطح الأرض
    public double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double distance = EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(distance * 100.0) / 100.0; // رقمين بعد الفاصلة
    }

    // رابط Maps URLs: مجاني وما يحتاج مفتاح، يفتح الاتجاهات مباشرة في قوقل ماب
    public String buildDirectionsUrl(Location location) {
        if (location.getLatitude() == null || location.getLongitude() == null) {
            return null;
        }
        // Locale.US: لو لغة السيرفر عربي، الأرقام تطلع ٢٤٫٧ ويخرب الرابط
        return "https://www.google.com/maps/dir/?api=1&destination="
                + String.format(Locale.US, "%.6f,%.6f", location.getLatitude(), location.getLongitude());
    }

    public void validateCoordinates(Double lat, Double lng) {
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new ApiException("Latitude must be between -90 and 90, longitude between -180 and 180");
        }
    }

    public void validateRadius(Double radiusKm) {
        if (radiusKm <= 0 || radiusKm > MAX_RADIUS_KM) {
            throw new ApiException("Radius must be greater than 0 and at most 50 km");
        }
    }

    // ================= Helpers =================

    private GoogleGeocodeResponse sendRequest(String uri, Object... uriVariables) {
        GoogleGeocodeResponse response;
        try {
            // المتغيرات تنحط في {} عشان Spring يسوي لها encoding (مهم للأسماء العربية والرموز)
            response = googleMapsRestClient.get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .body(GoogleGeocodeResponse.class);
        } catch (RestClientException e) {
            // ما نرجّع e.getMessage() لليوزر لأنها ممكن تحتوي الرابط ومعه المفتاح
            log.warn("Google Maps request failed: {}", e.getClass().getSimpleName());
            throw new ApiException("Google Maps request failed");
        }

        if (response == null) {
            throw new ApiException("Google Maps returned an empty response");
        }
        // قوقل غالباً يرجّع 200 حتى لو فيه خطأ، والخطأ الحقيقي يكون في status
        if ("ZERO_RESULTS".equals(response.status())) {
            throw new ApiException("Google Maps could not find this place");
        }
        if (!"OK".equals(response.status()) || response.results() == null || response.results().isEmpty()) {
            // REQUEST_DENIED غالباً مفتاح غلط أو الفوترة مو مفعّلة
            log.warn("Google Maps error: {} - {}", response.status(), response.errorMessage());
            throw new ApiException("Google Maps error: " + response.status());
        }
        return response;
    }

    // المدينة نطلعها من address_components (النوع locality)
    private AddressDTOOut toAddressDTO(GoogleGeocodeResponse.Result result) {
        String city = null;
        if (result.addressComponents() != null) {
            for (GoogleGeocodeResponse.AddressComponent component : result.addressComponents()) {
                if (component.types() != null && component.types().contains("locality")) {
                    city = component.longName();
                }
            }
        }
        GoogleGeocodeResponse.LatLng point = result.geometry().location();
        return new AddressDTOOut(point.lat(), point.lng(), result.formattedAddress(), city);
    }
}
