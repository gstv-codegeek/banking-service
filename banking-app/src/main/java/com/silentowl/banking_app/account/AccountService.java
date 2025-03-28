package com.silentowl.banking_app.account;

import com.silentowl.banking_app.user.CustomerTier;
import com.silentowl.banking_app.user.User;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(User customer, CustomerTier customerTier, BigDecimal initialDeposit);
    void lockAccount(Long accountId);
    void unlockAccount(Long accountId);
    Optional<List<AccountCreationResponse>> findAllAccounts();

    Optional<AccountCreationResponse> findAccountById(Long accountId);
}
