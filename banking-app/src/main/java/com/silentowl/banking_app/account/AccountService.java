package com.silentowl.banking_app.account;

import com.silentowl.banking_app.user.CustomerTier;
import com.silentowl.banking_app.user.User;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(User customer, CustomerTier customerTier, BigDecimal initialDeposit, AccountType accountType);

    void activateAccount(Long accountId);

    void closeAccount(Long accountId);

    void freezeAccount(Long accountId);

    List<AccountCreationResponse> findAllAccounts();

    List<AccountCreationResponse> findAccountById(Long accountId);
}
