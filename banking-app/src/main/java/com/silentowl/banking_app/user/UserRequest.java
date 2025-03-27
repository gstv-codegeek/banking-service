package com.silentowl.banking_app.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private Long id;
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @NotBlank(message = "Date of birth cannot be empty")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 16, message = "Password must be between 6 and 16 characters")
    //     @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String password;

    // Address details
    @NotBlank(message = "Street cannot be blank")
    private String street;
    @NotBlank(message = "City cannot be blank")
    private String city;
    @NotBlank(message = "State cannot be blank")
    private String state;
    @NotBlank(message = "Country cannot be blank")
    private String country;

    // Financial details
    @NotBlank(message = "Initial deposit cannot be blank")
    private BigDecimal initialDeposit;
    @NotBlank(message = "Annual Income cannot be blank")
    private BigDecimal annualIncome;
    @NotBlank(message = "Occupation cannot be blank")
    private String occupation;
}
