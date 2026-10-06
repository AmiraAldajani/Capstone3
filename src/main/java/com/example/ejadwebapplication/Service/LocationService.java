package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.LocationDTOIn;
import com.example.ejadwebapplication.DTOOUT.LocationDTOOut;
import com.example.ejadwebapplication.Enums.LocationType;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Repository.LocationRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final StaffRepository staffRepository;

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
