package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.LocationDTOIn;
import com.example.ejadwebapplication.DTOOUT.LocationDTOOut;
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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final StaffRepository staffRepository;
    private final ReportRepository reportRepository;

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
        locationRepository.save(location);
    }

    public void updateLocation(Integer id, LocationDTOIn dto) {
        Location location = locationRepository.findLocationById(id);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setCity(dto.getCity());
        location.setType(LocationType.valueOf(dto.getType()));
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

    private List<LocationDTOOut> convertListToDTO(List<Location> locations) {
        List<LocationDTOOut> result = new ArrayList<>();
        for (Location location : locations) {
            result.add(convertToDTO(location));
        }
        return result;
    }

    private LocationDTOOut convertToDTO(Location location) {
        return new LocationDTOOut(location.getId(), location.getName(), location.getDescription(),
                location.getCity(), location.getType().name());
    }
}