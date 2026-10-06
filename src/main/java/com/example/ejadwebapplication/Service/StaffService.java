package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.StaffDTOIn;
import com.example.ejadwebapplication.DTOOUT.StaffDTOOut;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Staff;
import com.example.ejadwebapplication.Repository.LocationRepository;
import com.example.ejadwebapplication.Repository.NotificationRepository;
import com.example.ejadwebapplication.Repository.ReportRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;

    public List<StaffDTOOut> getAllStaff() {
        return convertListToDTO(staffRepository.findAll());
    }

    public StaffDTOOut getStaffById(Integer id) {
        Staff staff = staffRepository.findStaffById(id);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        return convertToDTO(staff);
    }

    public void addStaff(StaffDTOIn dto) {
        Location location = locationRepository.findLocationById(dto.getLocationId());
        if (location == null) {
            throw new ApiException("Location not found");
        }
        if (staffRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (staffRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        Staff staff = new Staff();
        staff.setFullName(dto.getFullName());
        staff.setUsername(dto.getUsername());
        staff.setEmail(dto.getEmail());
        staff.setPassword(dto.getPassword());
        staff.setPhone(dto.getPhone());
        staff.setIsVerified(false); // ينتظر توثيق الأدمن
        staff.setLocation(location);
        staffRepository.save(staff);
    }

    public void updateStaff(Integer id, StaffDTOIn dto) {
        Staff staff = staffRepository.findStaffById(id);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        Location location = locationRepository.findLocationById(dto.getLocationId());
        if (location == null) {
            throw new ApiException("Location not found");
        }
        if (!staff.getUsername().equals(dto.getUsername()) && staffRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (!staff.getEmail().equals(dto.getEmail()) && staffRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        staff.setFullName(dto.getFullName());
        staff.setUsername(dto.getUsername());
        staff.setEmail(dto.getEmail());
        staff.setPassword(dto.getPassword());
        staff.setPhone(dto.getPhone());
        staff.setLocation(location);
        staffRepository.save(staff);
    }

    // الموظف يوصله إشعارات NEW_REPORT حتى لو ما رفع بلاغ، فنحذفها أول
    @Transactional
    public void deleteStaff(Integer id) {
        Staff staff = staffRepository.findStaffById(id);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        if (reportRepository.existsByStaff(staff)) {
            throw new ApiException("Cannot delete a staff member who has reports, delete the reports first");
        }
        notificationRepository.deleteAllByStaff(staff);
        staffRepository.delete(staff);
    }

    public List<StaffDTOOut> getStaffByLocation(Integer locationId) {
        Location location = locationRepository.findLocationById(locationId);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        return convertListToDTO(staffRepository.findAllByLocation(location));
    }

    public List<StaffDTOOut> getUnverifiedStaff() {
        return convertListToDTO(staffRepository.findAllByIsVerified(false));
    }

    private List<StaffDTOOut> convertListToDTO(List<Staff> staffList) {
        List<StaffDTOOut> result = new ArrayList<>();
        for (Staff staff : staffList) {
            result.add(convertToDTO(staff));
        }
        return result;
    }

    private StaffDTOOut convertToDTO(Staff staff) {
        Integer locationId = null;
        String locationName = null;
        if (staff.getLocation() != null) {
            locationId = staff.getLocation().getId();
            locationName = staff.getLocation().getName();
        }
        return new StaffDTOOut(staff.getId(), staff.getFullName(), staff.getUsername(),
                staff.getEmail(), staff.getPhone(), staff.getIsVerified(),
                locationId, locationName, staff.getCreatedAt());
    }
}