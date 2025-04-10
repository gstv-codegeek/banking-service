package com.silentowl.banking_app.transaction;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface TransactionService {

    void processDeposit(Long accountId, BigDecimal amount, Authentication connectedUser, boolean isNewCustomer);
    void processWithdrawal(Long accountId, BigDecimal amount, Authentication connectedUser);
    void processTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount, Authentication connectedUser);

}
