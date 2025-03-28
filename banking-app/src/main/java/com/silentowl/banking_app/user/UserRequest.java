package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.AccountType;
import com.silentowl.banking_app.kyc.KycVerificationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Date of birth cannot be empty")
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
    @NotNull(message = "Initial deposit cannot be blank")
    private BigDecimal initialDeposit;
    @NotNull(message = "Account Type cannot be blank")
    private AccountType accountType;
    @NotNull(message = "Annual Income cannot be blank")
    private BigDecimal annualIncome;
    @NotBlank(message = "Occupation cannot be blank")
    private String occupation;
}
