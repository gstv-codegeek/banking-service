package com.silentowl.banking_app.account;

import com.silentowl.banking_app.kyc.KycVerificationStatus;
import com.silentowl.banking_app.user.CustomerTier;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountCreationResponse {
    // personal details
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private CustomerTier customerTier;
    private KycVerificationStatus kycStatus;

    // address info
    private String street;
    private String city;
    private String state;
    private String country;

    // account info
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;




}
