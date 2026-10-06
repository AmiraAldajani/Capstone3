package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTO.ReportDTO;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Integer id) {
        return reportRepository.findById(id).orElseThrow(() -> new ApiException("Report not found with provided id"));
    }

    public Report addReport(ReportDTO reportDTO) {
        Report report = new Report();

        report.setType(reportDTO.getType());
        report.setTitle(reportDTO.getTitle());
        report.setDescription(reportDTO.getDescription());
        report.setColor(reportDTO.getColor());
        report.setBrand(reportDTO.getBrand());
        report.setImageUrl(reportDTO.getImageUrl());
        report.setItemDate(reportDTO.getItemDate());
        report.setUser(reportDTO.getUser());
        report.setCategory(reportDTO.getCategory());
        report.setLocations(reportDTO.getLocations());

        report.setStatus(ReportStatus.OPEN);
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    public Report updateReport(Integer id, ReportDTO reportDTO) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ApiException("Report not found to be updated"));

        report.setType(reportDTO.getType());
        report.setTitle(reportDTO.getTitle());
        report.setDescription(reportDTO.getDescription());
        report.setColor(reportDTO.getColor());
        report.setBrand(reportDTO.getBrand());
        report.setImageUrl(reportDTO.getImageUrl());
        report.setItemDate(reportDTO.getItemDate());
        report.setUser(reportDTO.getUser());
        report.setCategory(reportDTO.getCategory());
        report.setLocations(reportDTO.getLocations());

        return reportRepository.save(report);
    }

    public void deleteReport(Integer id) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ApiException("Report not found to be deleted"));
        reportRepository.delete(report);
    }

    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getReportsByCategory(Integer categoryId) {
        return reportRepository.findByCategoryId(categoryId);
    }

    public List<Report> getReportsByLocation(Integer locationId) {
        return reportRepository.findByLocationsId(locationId);
    }

    public List<Report> getReportsByDate(LocalDate date) {
        return reportRepository.findByItemDate(date);
    }

    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reportRepository.findByItemDateBetween(startDate, endDate);
    }

    public List<Report> searchReportsByTitle(String keyword) {
        return reportRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Report> searchReportsByDescription(String keyword) {
        return reportRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    public Report updateReportStatus(Integer id, ReportStatus status) {

        Report report = reportRepository.findById(id).orElseThrow(() -> new ApiException("Report not found to update status"));
        report.setStatus(status);
        return reportRepository.save(report);
    }

}
