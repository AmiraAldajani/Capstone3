package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StaffDTOOut {
    private Integer id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private Boolean isVerified;
    private Integer locationId;
    private String locationName;
    private LocalDateTime createdAt;
}
