package com.example.ejadwebapplication.DTOIN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// يُستخدم لإضافة وتعديل User و Admin، و StaffDTOIn يورث منه
@Getter
@Setter
@NoArgsConstructor
public class AccountDTOIn {

    @NotEmpty(message = "Full name is required")
    @Size(max = 50, message = "Full name must not exceed 50 characters")
    private String fullName;

    @NotEmpty(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotEmpty(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters and contain letters and numbers")
    private String password;

    @NotEmpty(message = "Phone is required")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone must start with 05 and be 10 digits")
    private String phone;
}
