package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.LocationDTOIn;
import com.example.ejadwebapplication.DTOOUT.AddressDTOOut;
import com.example.ejadwebapplication.DTOOUT.LocationDTOOut;
import com.example.ejadwebapplication.DTOOUT.NearbyLocationDTOOut;
import com.example.ejadwebapplication.Enums.LocationType;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Repository.LocationRepository;
import com.example.ejadwebapplication.Repository.ReportRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final StaffRepository staffRepository;
    private final ReportRepository reportRepository;
    private final GoogleMapsService googleMapsService;

    public List<LocationDTOOut> getAllLocations() {
        return convertListToDTO(locationRepository.findAll());
    }

    public LocationDTOOut getLocationById(Integer id) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        return convertToDTO(location);
    }

    public void addLocation(LocationDTOIn dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setCity(dto.getCity());
        location.setType(LocationType.valueOf(dto.getType()));
        setCoordinates(location, dto);
        locationRepository.save(location);
    }

    public void updateLocation(Integer id, LocationDTOIn dto) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        // نعرف قبل التعديل هل المكان نفسه تغيّر
        boolean placeChanged = !location.getName().equalsIgnoreCase(dto.getName())
                || !location.getCity().equalsIgnoreCase(dto.getCity());

        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setCity(dto.getCity());
        location.setType(LocationType.valueOf(dto.getType()));

        // ما نكلّم قوقل إلا إذا لزم: إحداثيات يدوية، أو تغيّر الاسم أو المدينة
        if (placeChanged || dto.getLatitude() != null || dto.getLongitude() != null) {
            setCoordinates(location, dto);
        }
        locationRepository.save(location);
    }

    public void deleteLocation(Integer id) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        if (staffRepository.existsByLocation(location)) {
            throw new ApiException("Cannot delete a location that has staff assigned to it");
        }
        if (reportRepository.existsByLocationsContaining(location)) {
            throw new ApiException("Cannot delete a location that is linked to reports");
        }
        locationRepository.delete(location);
    }

    public List<LocationDTOOut> getLocationsByCity(String city) {
        return convertListToDTO(locationRepository.findAllByCityIgnoreCase(city));
    }

    public List<LocationDTOOut> getLocationsByType(String type) {
        LocationType locationType;
        try {
            locationType = LocationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid location type");
        }
        return convertListToDTO(locationRepository.findAllByType(locationType));
    }

    // عدد الموظفين والبلاغات في مكان واحد
    public Map<String, Object> getLocationStatistics(Integer id) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("locationId", location.getId());
        stats.put("locationName", location.getName());
        stats.put("staffCount", staffRepository.countByLocation(location));
        stats.put("openReports", reportRepository.countByLocationsContainingAndStatus(location, ReportStatus.OPEN));
        stats.put("matchedReports", reportRepository.countByLocationsContainingAndStatus(location, ReportStatus.MATCHED));
        stats.put("closedReports", reportRepository.countByLocationsContainingAndStatus(location, ReportStatus.CLOSED));
        return stats;
    }

    // الأماكن مرتبة حسب عدد البلاغات (وين الأغراض تضيع أكثر)
    public List<Map<String, Object>> getTopLocations() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Location location : locationRepository.findAll()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("locationId", location.getId());
            item.put("locationName", location.getName());
            item.put("city", location.getCity());
            item.put("reportCount", reportRepository.countByLocationsContaining(location));
            result.add(item);
        }
        result.sort((a, b) -> Integer.compare((Integer) b.get("reportCount"), (Integer) a.get("reportCount")));
        return result;
    }

    // ================= Google Maps =================

    // للأماكن اللي انضافت قبل الربط مع قوقل (مثل بيانات الـ DataSeeder)
    public void geocodeLocation(Integer id) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        AddressDTOOut address = googleMapsService.geocode(location.getName() + ", " + location.getCity());
        location.setLatitude(address.getLatitude());
        location.setLongitude(address.getLongitude());
        locationRepository.save(location);
    }

    // أقرب الأماكن (مكاتب المفقودات) لموقع المستخدم، الأقرب أول
    public List<NearbyLocationDTOOut> getNearbyLocations(Double lat, Double lng, Double radiusKm) {
        googleMapsService.validateCoordinates(lat, lng);
        googleMapsService.validateRadius(radiusKm);

        List<NearbyLocationDTOOut> result = new ArrayList<>();
        for (Location location : locationRepository.findAllByLatitudeIsNotNullAndLongitudeIsNotNull()) {
            double distance = googleMapsService.distanceKm(lat, lng,
                    location.getLatitude(), location.getLongitude());
            if (distance <= radiusKm) {
                result.add(new NearbyLocationDTOOut(distance, convertToDTO(location)));
            }
        }
        result.sort(Comparator.comparing(NearbyLocationDTOOut::getDistanceKm));
        return result;
    }

    // يحوّل موقع الجوال لعنوان مقروء + اسم المدينة
    public AddressDTOOut reverseGeocode(Double lat, Double lng) {
        return googleMapsService.reverseGeocode(lat, lng);
    }

    // ================= Helpers =================

    // لو الأدمن أرسل الإحداثيات نعتمدها، غير كذا نطلبها من Google Maps
    private void setCoordinates(Location location, LocationDTOIn dto) {
        if ((dto.getLatitude() == null) != (dto.getLongitude() == null)) {
            throw new ApiException("Send both latitude and longitude, or neither");
        }
        if (dto.getLatitude() != null) {
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            return;
        }
        AddressDTOOut address = googleMapsService.geocode(dto.getName() + ", " + dto.getCity());
        location.setLatitude(address.getLatitude());
        location.setLongitude(address.getLongitude());
    }

    private List<LocationDTOOut> convertListToDTO(List<Location> locations) {
        List<LocationDTOOut> result = new ArrayList<>();
        for (Location location : locations) {
            result.add(convertToDTO(location));
        }
        return result;
    }

    private LocationDTOOut convertToDTO(Location location) {
        return new LocationDTOOut(location.getId(), location.getName(), location.getDescription(),
                location.getCity(), location.getType().name(),
                location.getLatitude(), location.getLongitude(),
                googleMapsService.buildDirectionsUrl(location));
    }
}