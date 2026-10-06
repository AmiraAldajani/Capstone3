package com.example.ejadwebapplication.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50) not null")
    private String fullName;

    @Column(columnDefinition = "varchar(30) not null unique")
    private String username;

    @Column(columnDefinition = "varchar(100) not null unique")
    private String email;

    @Column(columnDefinition = "varchar(255) not null")
    private String password;

    @Column(columnDefinition = "varchar(10) not null")
    private String phone;

    @Column(columnDefinition = "datetime not null", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
