package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByType(ReportType type);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByCategoryId(Integer categoryId);
    List<Report> findByLocationsId(Integer locationId);
    List<Report> findByItemDate(LocalDate date);
    List<Report> findByItemDateBetween(LocalDate startDate, LocalDate endDate);
    List<Report> findByTitleContainingIgnoreCase(String title);
    List<Report> findByDescriptionContainingIgnoreCase(String description);
}

