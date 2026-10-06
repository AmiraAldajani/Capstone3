package com.example.ejadwebapplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// نتيجة مقارنة بلاغ واحد من الـ AI
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResultDTO {
    private Integer reportId;
    private Double score;
    private String reason;
}