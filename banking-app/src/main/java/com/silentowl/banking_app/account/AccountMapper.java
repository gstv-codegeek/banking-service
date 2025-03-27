package com.silentowl.banking_app.account;

import com.silentowl.banking_app.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountMapper {

//    public Account mapToAccountEntity(String iban, User user) {
//        return Account.builder()
//                .iban(iban)
//                .user(user)
//                .status(AccountStatus.ACTIVE)
//                .currency("KES")
//                .accountType(AccountType.CHECKING)
//                .balance(BigDecimal.ZERO)
//                .build();
//    }

    public AccountCreationResponse mapToAccountResponse(Account account) {
        return AccountCreationResponse.builder()
                .accountNumber(account.getIban())
                .firstName(account.getUser().getFirstName())
                .lastName(account.getUser().getLastName())
                .email(account.getUser().getEmail())

                .street(account.getUser().getStreet())
                .city(account.getUser().getCity())
                .state(account.getUser().getState())
                .country(account.getUser().getCountry())

                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .currency(account.getCurrency())
                .accountStatus(account.getStatus())
                .build();
    }
}
