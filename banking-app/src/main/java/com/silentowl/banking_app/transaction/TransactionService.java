package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface TransactionService {

    void processDeposit(Long accountId, BigDecimal amount, Authentication connectedUser);
    void processWithdrawal(Long accountId, BigDecimal amount, Authentication connectedUser);
    void processTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount, Authentication connectedUser);
}
