package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StaffDTOIn extends AccountDTOIn {

    @NotNull(message = "Location id is required")
    private Integer locationId;
}
