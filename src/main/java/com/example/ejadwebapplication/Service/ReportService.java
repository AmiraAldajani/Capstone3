package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.ReportDTOIn;
import com.example.ejadwebapplication.DTOOUT.LocationDTOOut;
import com.example.ejadwebapplication.DTOOUT.NearbyReportDTOOut;
import com.example.ejadwebapplication.DTOOUT.ReportDTOOut;
import com.example.ejadwebapplication.Enums.MatchStatus;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.*;
import com.example.ejadwebapplication.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final NotificationRepository notificationRepository;
    private final ReportMatchRepository reportMatchRepository;
    private final NotificationService notificationService;
    private final ReportMatchService reportMatchService;
    private final GoogleMapsService googleMapsService;

    public List<ReportDTOOut> getAllReports() {
        return convertListToDTO(reportRepository.findAll());
    }

    public ReportDTOOut getReportById(Integer id) {
        return convertToDTO(findReport(id));
    }

    // البلاغ + إشعارات الموظفين + التطابقات، كلها في نفس الـ transaction
    @Transactional
    public ReportDTOOut addReport(ReportDTOIn dto) {
        ReportType type = ReportType.valueOf(dto.getType());

        Report report = new Report();
        report.setType(type);
        setOwner(report, dto.getUserId(), dto.getStaffId(), type);
        fillDetails(report, dto, type);

        Report saved = reportRepository.save(report);

        notificationService.notifyStaffAboutNewReport(saved);
        reportMatchService.findMatchesForReport(saved);

        return convertToDTO(saved);
    }

    // لو انضاف مكان جديد، موظفينه يوصلهم إشعار
    // التعديل يغيّر التفاصيل بس، أما النوع وصاحب البلاغ يتجاهلهم
// بعد التعديل: إشعار لموظفين الأماكن الجديدة + نبحث عن تطابقات على التفاصيل الجديدة
    @Transactional
    public ReportDTOOut updateReport(Integer id, ReportDTOIn dto) {
        Report report = findReport(id);
        if (report.getStatus() != ReportStatus.OPEN) {
            throw new ApiException("Only open reports can be updated");
        }

        // نحفظ ids الأماكن القديمة قبل ما fillDetails يستبدلها
        Set<Integer> oldLocationIds = new HashSet<>();
        for (Location location : report.getLocations()) {
            oldLocationIds.add(location.getId());
        }

        fillDetails(report, dto, report.getType());
        Report saved = reportRepository.save(report);

        // الأماكن اللي ما كانت موجودة قبل التعديل
        Set<Location> addedLocations = new HashSet<>();
        for (Location location : saved.getLocations()) {
            if (!oldLocationIds.contains(location.getId())) {
                addedLocations.add(location);
            }
        }
        notificationService.notifyStaffAtLocations(saved, addedLocations);
        reportMatchService.findMatchesForReport(saved);
        return convertToDTO(saved);
    }

    // نحذف الإشعارات والتطابقات أول، لأنها تأشر على البلاغ (foreign key)
    @Transactional
    public void deleteReport(Integer id) {
        Report report = findReport(id);
        if (report.getStatus() == ReportStatus.MATCHED) {
            throw new ApiException("Cannot delete a report that has a confirmed match");
        }
        notificationRepository.deleteAllByReport(report);
        reportMatchRepository.deleteAll(reportMatchRepository.findAllByLostReportOrFoundReport(report, report));
        reportRepository.delete(report);
    }

    // إغلاق البلاغ (مثلاً استلم صاحبه غرضه)، والاقتراحات المعلقة ترتفض
    @Transactional
    public void closeReport(Integer id) {
        Report report = findReport(id);
        if (report.getStatus() == ReportStatus.CLOSED) {
            throw new ApiException("Report is already closed");
        }
        report.setStatus(ReportStatus.CLOSED);
        reportRepository.save(report);

        for (ReportMatch match : reportMatchRepository.findAllByLostReportOrFoundReport(report, report)) {
            if (match.getStatus() == MatchStatus.SUGGESTED) {
                match.setStatus(MatchStatus.REJECTED);
                reportMatchRepository.save(match);
            }
        }
    }

    // ================= Filters =================

    public List<ReportDTOOut> getReportsByUser(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        return convertListToDTO(reportRepository.findAllByUser(user));
    }

    public List<ReportDTOOut> getReportsByStaff(Integer staffId) {
        Staff staff = staffRepository.findStaffById(staffId);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        return convertListToDTO(reportRepository.findAllByStaff(staff));
    }

    public List<ReportDTOOut> getReportsByStatus(String status) {
        ReportStatus reportStatus;
        try {
            reportStatus = ReportStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Status must be OPEN, MATCHED or CLOSED");
        }
        return convertListToDTO(reportRepository.findAllByStatus(reportStatus));
    }

    public List<ReportDTOOut> getReportsByType(String type) {
        ReportType reportType;
        try {
            reportType = ReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Type must be LOST or FOUND");
        }
        return convertListToDTO(reportRepository.findAllByType(reportType));
    }

    public List<ReportDTOOut> getReportsByLocation(Integer locationId) {
        Location location = locationRepository.findLocationById(locationId);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        return convertListToDTO(reportRepository.findAllByLocationsContaining(location));
    }

    // ================= Helpers =================

    private Report findReport(Integer id) {
        Report report = reportRepository.findReportById(id);
        if (report == null) {
            throw new ApiException("Report not found");
        }
        return report;
    }

    // واحد بالضبط: user أو staff
    private void setOwner(Report report, Integer userId, Integer staffId, ReportType type) {
        if ((userId == null) == (staffId == null)) {
            throw new ApiException("Report must have exactly one owner: userId or staffId");
        }

        if (userId != null) {
            User user = userRepository.findUserById(userId);
            if (user == null) {
                throw new ApiException("User not found");
            }
            report.setUser(user);
            return;
        }

        Staff staff = staffRepository.findStaffById(staffId);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        if (!staff.getIsVerified()) {
            throw new ApiException("Staff must be verified by admin before creating reports");
        }
        // الموظف يرفع FOUND بس (غرض انسلّم له)
        if (type != ReportType.FOUND) {
            throw new ApiException("Staff can only create FOUND reports");
        }
        report.setStaff(staff);
    }

    private void fillDetails(Report report, ReportDTOIn dto, ReportType type) {
        Category category = categoryRepository.findCategoryById(dto.getCategoryId());
        if (category == null) {
            throw new ApiException("Category not found");
        }

        report.setTitle(dto.getTitle());
        report.setDescription(dto.getDescription());
        report.setColor(dto.getColor());
        report.setBrand(dto.getBrand());
        report.setImageUrl(dto.getImageUrl());
        report.setItemDate(dto.getItemDate());
        report.setCategory(category);
        report.setLocations(getLocations(dto.getLocationIds(), type));
        checkStaffLocation(report);
    }

    // الموظف يرفع بلاغ في مكانه هو بس
    private void checkStaffLocation(Report report) {
        if (report.getStaff() == null) {
            return;
        }
        Location staffLocation = report.getStaff().getLocation();
        for (Location location : report.getLocations()) {
            if (staffLocation == null || !location.getId().equals(staffLocation.getId())) {
                throw new ApiException("Staff can only create reports in their own location");
            }
        }
    }

    // FOUND: مكان واحد بالضبط | LOST: حد أقصى 3
    private Set<Location> getLocations(Set<Integer> locationIds, ReportType type) {
        if (type == ReportType.FOUND && locationIds.size() != 1) {
            throw new ApiException("Found report must have exactly one location");
        }
        if (type == ReportType.LOST && locationIds.size() > 3) {
            throw new ApiException("Lost report can have at most 3 locations");
        }

        Set<Location> locations = new HashSet<>();
        for (Integer locationId : locationIds) {
            Location location = locationRepository.findLocationById(locationId);
            if (location == null) {
                throw new ApiException("Location not found with id: " + locationId);
            }
            locations.add(location);
        }
        return locations;
    }

    private List<ReportDTOOut> convertListToDTO(List<Report> reports) {
        List<ReportDTOOut> result = new ArrayList<>();
        for (Report report : reports) {
            result.add(convertToDTO(report));
        }
        return result;
    }

    private ReportDTOOut convertToDTO(Report report) {
        Integer userId = null;
        Integer staffId = null;
        String reporterName = null;
        if (report.getUser() != null) {
            userId = report.getUser().getId();
            reporterName = report.getUser().getFullName();
        } else if (report.getStaff() != null) {
            staffId = report.getStaff().getId();
            reporterName = report.getStaff().getFullName();
        }

        List<LocationDTOOut> locations = new ArrayList<>();
        if (report.getLocations() != null) {
            for (Location location : report.getLocations()) {
                locations.add(new LocationDTOOut(location.getId(), location.getName(),
                        location.getDescription(), location.getCity(), location.getType().name(),
                        location.getLatitude(), location.getLongitude(),
                        googleMapsService.buildDirectionsUrl(location)));
            }
        }

        return new ReportDTOOut(report.getId(), report.getType().name(), report.getTitle(),
                report.getDescription(), report.getColor(), report.getBrand(), report.getImageUrl(),
                report.getItemDate(), report.getStatus().name(), report.getCreatedAt(),
                userId, staffId, reporterName,
                report.getCategory().getId(), report.getCategory().getName(),
                locations);
    }
    public List<NearbyReportDTOOut> getNearbyFoundReports(Integer reportId, Double radiusKm) {
        googleMapsService.validateRadius(radiusKm);
        Report lostReport = findReport(reportId);
        if (lostReport.getType() != ReportType.LOST) {
            throw new ApiException("Nearby search is only for LOST reports");
        }
        if (lostReport.getStatus() != ReportStatus.OPEN) {
            throw new ApiException("Only open reports can search for nearby items");
        }

        List<NearbyReportDTOOut> result = new ArrayList<>();
        for (Report foundReport : reportRepository.findAllByTypeAndCategoryAndStatus(
                ReportType.FOUND, lostReport.getCategory(), ReportStatus.OPEN)) {
            Double distance = closestDistance(lostReport.getLocations(), foundReport.getLocations());
            if (distance != null && distance <= radiusKm) {
                result.add(new NearbyReportDTOOut(distance, convertToDTO(foundReport)));
            }
        }
        result.sort(Comparator.comparing(NearbyReportDTOOut::getDistanceKm));
        return result;
    }

    // Shortest distance between any lost location and any found location; skips locations without coordinates
    private Double closestDistance(Set<Location> lostLocations, Set<Location> foundLocations) {
        Double closest = null;
        for (Location lost : lostLocations) {
            for (Location found : foundLocations) {
                if (lost.getLatitude() == null || lost.getLongitude() == null
                        || found.getLatitude() == null || found.getLongitude() == null) {
                    continue;
                }
                double distance = googleMapsService.distanceKm(lost.getLatitude(), lost.getLongitude(),
                        found.getLatitude(), found.getLongitude());
                if (closest == null || distance < closest) {
                    closest = distance;
                }
            }
        }
        return closest;
    }

}