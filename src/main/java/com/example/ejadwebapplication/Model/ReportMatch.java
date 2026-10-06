package com.example.ejadwebapplication.Model;

import com.example.ejadwebapplication.Enums.MatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReportMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Similarity score is required")
    @DecimalMin(value = "0.0", message = "Similarity score must be at least 0")
    @DecimalMax(value = "100.0", message = "Similarity score must be at most 100")
    @Column(columnDefinition = "double not null")
    private Double similarityScore;

    @Size(max = 500, message = "AI reason must be at most 500 characters")
    @Column(columnDefinition = "varchar(500)")
    private String aiReason;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) not null")
    private MatchStatus status;

    @Column(columnDefinition = "datetime not null", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "lost_report_id", nullable = false)
    private Report lostReport;

    @ManyToOne
    @JoinColumn(name = "found_report_id", nullable = false)
    private Report foundReport;

    @PrePersist
    public void onCreate() {
        status = MatchStatus.SUGGESTED;
        createdAt = LocalDateTime.now();
    }
}