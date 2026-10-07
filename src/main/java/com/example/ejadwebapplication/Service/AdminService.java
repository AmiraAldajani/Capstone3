package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.AccountDTOIn;
import com.example.ejadwebapplication.DTOOUT.AdminDTOOut;
import com.example.ejadwebapplication.Enums.MatchStatus;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.Admin;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Model.ReportMatch;
import com.example.ejadwebapplication.Model.Staff;
import com.example.ejadwebapplication.Repository.AdminRepository;
import com.example.ejadwebapplication.Repository.LocationRepository;
import com.example.ejadwebapplication.Repository.ReportMatchRepository;
import com.example.ejadwebapplication.Repository.ReportRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import com.example.ejadwebapplication.Repository.UserRepository;
import com.example.ejadwebapplication.Client.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportMatchRepository reportMatchRepository;
    private final LocationRepository locationRepository;
    private final EmailSender emailSender;

    public List<AdminDTOOut> getAllAdmins() {
        List<AdminDTOOut> admins = new ArrayList<>();
        for (Admin admin : adminRepository.findAll()) {
            admins.add(convertToDTO(admin));
        }
        return admins;
    }

    public AdminDTOOut getAdminById(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        return convertToDTO(admin);
    }

    public void addAdmin(AccountDTOIn dto) {
        if (adminRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setFullName(dto.getFullName());
        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setPhone(dto.getPhone());
        adminRepository.save(admin);
    }

    public void updateAdmin(Integer id, AccountDTOIn dto) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        if (!admin.getUsername().equals(dto.getUsername()) && adminRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (!admin.getEmail().equals(dto.getEmail()) && adminRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        admin.setFullName(dto.getFullName());
        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setPhone(dto.getPhone());
        adminRepository.save(admin);
    }

    public void deleteAdmin(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        adminRepository.delete(admin);
    }

    // الأدمن يوثّق حساب موظف
    public void verifyStaff(Integer adminId, Integer staffId) {
        Admin admin = adminRepository.findAdminById(adminId);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        Staff staff = staffRepository.findStaffById(staffId);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        if (staff.getIsVerified()) {
            throw new ApiException("Staff is already verified");
        }

        staff.setIsVerified(true);
        staffRepository.save(staff);
        emailSender.sendStaffVerified(staff.getEmail(), staff.getFullName(), staff.getLocation().getName());
    }

    // ================= Extra =================

    // سحب التوثيق (مثلاً الموظف ترك العمل)، بعدها ما يقدر يرفع بلاغات ولا يوصله إشعارات
    public void unverifyStaff(Integer adminId, Integer staffId) {
        Admin admin = adminRepository.findAdminById(adminId);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        Staff staff = staffRepository.findStaffById(staffId);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        if (!staff.getIsVerified()) {
            throw new ApiException("Staff is not verified");
        }
        staff.setIsVerified(false);
        staffRepository.save(staff);
        emailSender.sendStaffUnverified(staff.getEmail(), staff.getFullName());
    }

    // أرقام عامة للوحة تحكم الأدمن
    public Map<String, Object> getStatistics(Integer adminId) {
        if (adminRepository.findAdminById(adminId) == null) {
            throw new ApiException("Admin not found");
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStaff", staffRepository.count());
        stats.put("unverifiedStaff", staffRepository.countByIsVerified(false));
        stats.put("openReports", reportRepository.countByStatus(ReportStatus.OPEN));
        stats.put("matchedReports", reportRepository.countByStatus(ReportStatus.MATCHED));
        stats.put("closedReports", reportRepository.countByStatus(ReportStatus.CLOSED));
        stats.put("suggestedMatches", reportMatchRepository.countByStatus(MatchStatus.SUGGESTED));
        stats.put("confirmedMatches", reportMatchRepository.countByStatus(MatchStatus.CONFIRMED));
        return stats;
    }

    // ================= Extra 2 =================

    // يقفل البلاغات المفتوحة من أكثر من X يوم، ويرفض اقتراحاتها المعلقة
    @Transactional
    public Integer closeOldReports(Integer adminId, Integer days) {
        checkAdmin(adminId);
        if (days == null || days < 1) {
            throw new ApiException("Days must be at least 1");
        }
        List<Report> oldReports = reportRepository.findAllByStatusAndCreatedAtBefore(
                ReportStatus.OPEN, LocalDateTime.now().minusDays(days));

        List<ReportMatch> rejected = new ArrayList<>();
        for (Report report : oldReports) {
            report.setStatus(ReportStatus.CLOSED);
            for (ReportMatch match : reportMatchRepository.findAllByLostReportOrFoundReport(report, report)) {
                if (match.getStatus() == MatchStatus.SUGGESTED) {
                    match.setStatus(MatchStatus.REJECTED);
                    rejected.add(match);
                }
            }
        }
        reportRepository.saveAll(oldReports);
        reportMatchRepository.saveAll(rejected);
        return oldReports.size();
    }

    // كل تطابق مؤكد = بلاغ LOST واحد انحل، فالنسبة = المؤكدة / كل بلاغات LOST
    public Map<String, Object> getSuccessRate(Integer adminId) {
        checkAdmin(adminId);
        int totalLost = reportRepository.countByType(ReportType.LOST);
        int matchedLost = reportMatchRepository.countByStatus(MatchStatus.CONFIRMED);
        double rate = totalLost == 0 ? 0.0 : Math.round(matchedLost * 1000.0 / totalLost) / 10.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalLostReports", totalLost);
        result.put("matchedLostReports", matchedLost);
        result.put("successRate", rate + "%");
        return result;
    }

    // توثيق كل الموظفين المنتظرين في مكان واحد
    public Integer verifyAllStaffAtLocation(Integer adminId, Integer locationId) {
        checkAdmin(adminId);
        Location location = locationRepository.findLocationById(locationId);
        if (location == null) {
            throw new ApiException("Location not found");
        }
        List<Staff> pending = new ArrayList<>();
        for (Staff staff : staffRepository.findAllByLocation(location)) {
            if (!staff.getIsVerified()) {
                staff.setIsVerified(true);
                pending.add(staff);
            }
        }
        if (pending.isEmpty()) {
            throw new ApiException("No unverified staff at this location");
        }
        staffRepository.saveAll(pending);
        for (Staff staff : pending) {
            emailSender.sendStaffVerified(staff.getEmail(), staff.getFullName(), location.getName());
        }
        return pending.size();
    }

    private void checkAdmin(Integer adminId) {
        if (adminRepository.findAdminById(adminId) == null) {
            throw new ApiException("Admin not found");
        }
    }

    private AdminDTOOut convertToDTO(Admin admin) {
        return new AdminDTOOut(admin.getId(), admin.getFullName(), admin.getUsername(),
                admin.getEmail(), admin.getPhone(), admin.getCreatedAt());
    }
}