package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDTOOut {
    private Integer id;
    private String name;
    private String description;
    private String city;
    private String type;
}
