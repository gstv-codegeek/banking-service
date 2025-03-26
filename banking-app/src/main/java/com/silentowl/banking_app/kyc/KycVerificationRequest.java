package com.silentowl.banking_app.kyc;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycVerificationRequest {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private BigDecimal initialDeposit;
    private BigDecimal annualIncome;
    private String occupation;
}
