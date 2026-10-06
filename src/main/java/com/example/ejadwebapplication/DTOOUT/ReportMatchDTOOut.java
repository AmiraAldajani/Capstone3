package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReportMatchDTOOut {
    private Integer id;
    private Double similarityScore;
    private String aiReason;
    private String status;
    private LocalDateTime createdAt;
    private Integer lostReportId;
    private String lostReportTitle;
    private Integer foundReportId;
    private String foundReportTitle;
}