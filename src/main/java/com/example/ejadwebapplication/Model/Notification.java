package com.example.ejadwebapplication.Model;

import com.example.ejadwebapplication.Enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) not null")
    private NotificationType type;

    @NotEmpty(message = "Message is required")
    @Size(max = 300, message = "Message must be at most 300 characters")
    @Column(columnDefinition = "varchar(300) not null")
    private String message;

    @Column(columnDefinition = "boolean not null")
    private Boolean isRead = false;

    @Column(columnDefinition = "datetime not null", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @PrePersist
    public void onCreate() {
        if (isRead == null) {
            isRead = false;
        }
        createdAt = LocalDateTime.now();
    }
}
