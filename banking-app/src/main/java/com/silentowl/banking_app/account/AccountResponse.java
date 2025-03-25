package com.silentowl.banking_app.account;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private String street;
    private String city;
    private String state;
    private String country;

    private String iban;
    private BigDecimal balance;
    private String accountType;
    private String currency;
    private boolean locked;
}
