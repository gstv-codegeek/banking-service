package com.silentowl.banking_app.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    // personal info
    private String firstName;
    private Long id;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    // address info
    private String street;
    private String city;
    private String state;
    private String country;
    // financial info
    private BigDecimal initialDeposit;
    private BigDecimal annualIncome;
    private String occupation;
}
