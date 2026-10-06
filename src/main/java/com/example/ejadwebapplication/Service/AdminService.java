package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.AccountDTOIn;
import com.example.ejadwebapplication.DTOOUT.AdminDTOOut;
import com.example.ejadwebapplication.Model.Admin;
import com.example.ejadwebapplication.Model.Staff;
import com.example.ejadwebapplication.Repository.AdminRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;

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
    }

    private AdminDTOOut convertToDTO(Admin admin) {
        return new AdminDTOOut(admin.getId(), admin.getFullName(), admin.getUsername(),
                admin.getEmail(), admin.getPhone(), admin.getCreatedAt());
    }
}
