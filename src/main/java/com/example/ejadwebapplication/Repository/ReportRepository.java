package com.example.ejadwebapplication.Repository;


import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    Report findReportById(Integer id);

    List<Report> findAllByUser(User user);

    List<Report> findAllByStaff(Staff staff);

    List<Report> findAllByStatus(ReportStatus status);

    List<Report> findAllByType(ReportType type);

    List<Report> findAllByLocationsContaining(Location location);

    Boolean existsByUser(User user);

    Boolean existsByStaff(Staff staff);

    Boolean existsByCategory(Category category);

    Boolean existsByLocationsContaining(Location location);

    @Query("select distinct r from Report r join r.locations l " +
            "where r.type = ?1 and r.category = ?2 and r.status = ?3 and l in ?4")
    List<Report> findMatchCandidates(
            ReportType type,
            Category category,
            ReportStatus status,
            Set<Location> locations
    );

    // Extra Endpoints

    List<Report> findByCategoryId(Integer categoryId);

    List<Report> findByLocationsId(Integer locationId);

    List<Report> findByItemDate(LocalDate date);

    List<Report> findByItemDateBetween(LocalDate startDate, LocalDate endDate);

    List<Report> findByTitleContainingIgnoreCase(String title);

    List<Report> findByDescriptionContainingIgnoreCase(String description);

}
