package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// بدل ما نستقبل ReportMatch كامل، نستقبل الـ ids بس
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportMatchDTOIn {

    @NotNull(message = "Lost report id is required")
    private Integer lostReportId;

    @NotNull(message = "Found report id is required")
    private Integer foundReportId;

    @NotNull(message = "Similarity score is required")
    @DecimalMin(value = "0.0", message = "Similarity score must be at least 0")
    @DecimalMax(value = "100.0", message = "Similarity score must be at most 100")
    private Double similarityScore;

    @Size(max = 500, message = "AI reason must be at most 500 characters")
    private String aiReason;
}