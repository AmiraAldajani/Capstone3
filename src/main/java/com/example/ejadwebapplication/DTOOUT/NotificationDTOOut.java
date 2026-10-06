package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationDTOOut {
    private Integer id;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Integer userId;
    private Integer staffId;
    private Integer reportId;
    private String reportTitle;
}