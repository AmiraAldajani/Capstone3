package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDTOOut {
    private Integer id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
