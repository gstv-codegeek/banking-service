package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;

import java.math.BigDecimal;

public interface TransactionService {

    void processDeposit(Long accountId, BigDecimal amount);
    void processWithdrawal(Long accountId, BigDecimal amount);
    void processTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount);
}
