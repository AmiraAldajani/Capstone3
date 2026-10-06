package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    Staff findStaffById(Integer id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<Staff> findAllByLocation(Location location);

    List<Staff> findAllByIsVerified(Boolean isVerified);

    Boolean existsByLocation(Location location);

    // الموظفين الموثّقين بس هم اللي يوصلهم إشعار NEW_REPORT
    List<Staff> findAllByLocationAndIsVerifiedTrue(Location location);
}