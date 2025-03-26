package com.silentowl.banking_app.account;

import com.silentowl.banking_app.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountMapper {

    public Account mapToAccountEntity(String iban, User user) {
        return Account.builder()
                .iban(iban)
                .user(user)
                .locked(true)
                .currency("KES")
                .accountType("SAVINGS_ACCOUNT")
                .balance(BigDecimal.ZERO)
                .build();
    }

    public AccountCreationResponse mapToAccountResponse(Account account) {
        return AccountCreationResponse.builder()
                .id(account.getId())
                .firstName(account.getUser().getFirstName())
                .lastName(account.getUser().getLastName())
                .street(account.getUser().getStreet())
                .city(account.getUser().getCity())
                .state(account.getUser().getState())
                .country(account.getUser().getCountry())
                .email(account.getUser().getEmail())
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .locked(account.isLocked())
                .build();
    }
}
