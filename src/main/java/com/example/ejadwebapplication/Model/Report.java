package com.example.ejadwebapplication.Model;

import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
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
    private ReportType type;

    private String title;

    private String description;

    private String color;

    private String brand;

    private String imageUrl;

    @NotNull
    private LocalDate itemDate;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(name = "report_location", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
    private Set<Location> locations;

    @OneToMany(mappedBy = "report")
    private Set<Notification> notifications;
}
