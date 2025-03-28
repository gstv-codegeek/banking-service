package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.exceptions.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends Transaction{

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public void processDeposit(Account account, BigDecimal amount) {
        validateAmount(amount);

        // Credit transaction
        Transaction creditTx = createTransaction(
                account,
                amount,
                TransactionType.DEPOSIT,
                TransactionDirection.CREDIT,
                "Deposit Received"
        );

        transactionRepository.save(creditTx);

        // update account balance
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }


    @Transactional
    public void processWithdrawal(Account account, BigDecimal amount) {
        validateAmount(amount);
        validateSufficientFunds(account, amount);

        // debit transaction for withdrawing account
        Transaction debitTx = createTransaction(
                account,
                amount,
                TransactionType.WITHDRAWAL,
                TransactionDirection.DEBIT,
                "Withdrawal"
        ) ;
        transactionRepository.save(debitTx);

        // Update account balance
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }


    @Transactional
    public void processTransfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
        validateAmount(amount);
        validateSufficientFunds(sourceAccount, amount);

        // debit transaction for sending account
        Transaction debitTx = createTransaction(
                sourceAccount,
                amount,
                TransactionType.TRANSFER_SENT,
                TransactionDirection.DEBIT,
                "Transfer to " + destinationAccount.getIban()
        );

        // credit transaction for the receiving account
        Transaction creditTx = createTransaction(
                destinationAccount,
                amount,
                TransactionType.TRANSFER_RECEIVED,
                TransactionDirection.CREDIT,
                "Transfer from " + sourceAccount.getIban()
        );

        // save both transactions
        transactionRepository.save(debitTx);
        transactionRepository.save(creditTx);

        // update account balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.saveAll(List.of(sourceAccount, destinationAccount));
    }

    private Transaction createTransaction(
            Account account,
            BigDecimal amount,
            TransactionType type,
            TransactionDirection direction,
            String description
    ) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDirection(direction);
        tx.setDescription(description);

        return tx;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Transaction amount must be positive");
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) throw new InsufficientFundsException("Insufficient funds for transaction");
    }
}
