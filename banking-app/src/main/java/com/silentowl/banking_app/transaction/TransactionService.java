package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;

import java.math.BigDecimal;

public interface TransactionService {

    void processDeposit(Account account, BigDecimal amount);
    void processWithdrawal(Account account, BigDecimal amount);
    void processTransfer(Account sourceAccount, Account destinationAccount, BigDecimal amount);
}
