package com.example.ejadwebapplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// json_object لازم يرجع object، فنغلّف القائمة داخل "matches"
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMatchResponse {
    private List<MatchResultDTO> matches;
}