package com.example.ejadwebapplication.Model;

import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10) not null")
    private ReportType type;

    @Column(columnDefinition = "varchar(50) not null")
    private String title;

    @Column(columnDefinition = "varchar(200) not null")
    private String description;

    @Column(columnDefinition = "varchar(30) not null")
    private String color;

    @Column(columnDefinition = "varchar(50)")
    private String brand;

    @Column(columnDefinition = "varchar(500)")
    private String imageUrl;

    @Column(columnDefinition = "date not null")
    private LocalDate itemDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10) not null")
    private ReportStatus status;

    @Column(columnDefinition = "datetime not null", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(name = "report_location",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private Set<Location> locations;

    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private Set<Notification> notifications;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = ReportStatus.OPEN;
        }
        createdAt = LocalDateTime.now();
    }
}
