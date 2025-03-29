package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.exceptions.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public void processDeposit(Long accountId, BigDecimal amount, Authentication connectedUser) {

        validateUser(connectedUser, accountId); // ensure user owns account
        validateAmount(amount);

        // Lock account to prevent race conditions
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> {
                    log.error("Deposit failed: Account {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        log.info("Processing deposit of {} to account {}", amount, accountId);
        // create & save credit transaction
        Transaction creditTx = createTransaction(
                account, amount, TransactionType.DEPOSIT,
                TransactionDirection.CREDIT, "Deposit Received"
        );
        transactionRepository.save(creditTx);

        // atomic balance update
        accountRepository.updateBalance(accountId, amount);

        log.info("Deposit of {} to account {} completed successfully", amount, accountId);
    }


    @Transactional
    public void processWithdrawal(Long accountId, BigDecimal amount, Authentication connectedUser) {
        validateUser(connectedUser, accountId); // ensure user owns account
        validateAmount(amount);

        // Lock account to prevent race conditions
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> {
                    log.error("Withdrawal failed: Account {} not found", accountId);
                    return new RuntimeException("Account not found");
                });
        validateSufficientFunds(account, amount, "withdraw");

        log.info("Processing withdraw of {} from account {}", amount, accountId);
        // debit transaction for withdrawing account
        Transaction debitTx = createTransaction(
                account, amount, TransactionType.WITHDRAWAL,
                TransactionDirection.DEBIT, "Withdrawal");
        transactionRepository.save(debitTx);

        // atomic balance update
        accountRepository.updateBalance(accountId, amount.negate());

        log.info("Withdrawal of {} from account {} completed successfully", amount, accountId);
    }


    @Transactional
    public void processTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount, Authentication connectedUser) {
        validateUser(connectedUser, sourceAccountId);
        validateAmount(amount);

        // Lock accounts to prevent race conditions
        Account sourceAccount = accountRepository.findByIdWithLock(sourceAccountId)
                .orElseThrow(() -> {
                    log.error("Transfer failed: Source account {} not", sourceAccountId);
                    return new RuntimeException("Source account not found");
                });
        Account destinationAccount = accountRepository.findByIdWithLock(destinationAccountId)
                .orElseThrow(() -> {
                    log.error("Transfer failed: Destination account {} not", destinationAccountId);
                    return new RuntimeException("Destination account not found");
                });

        validateSufficientFunds(sourceAccount, amount, "transfer");

        log.info("Processing transfer of {} from account {} to account {}", amount, sourceAccountId, destinationAccountId);
        // create transactions
        Transaction debitTx = createTransaction(
                sourceAccount, amount, TransactionType.TRANSFER_SENT,
                TransactionDirection.DEBIT, "Transfer to " + destinationAccount.getIban());
        Transaction creditTx = createTransaction(
                destinationAccount, amount, TransactionType.TRANSFER_RECEIVED,
                TransactionDirection.CREDIT, "Transfer from " + sourceAccount.getIban());

        // save both transactions
        transactionRepository.saveAll(List.of(debitTx, creditTx));

        // atomic account balance updates
        accountRepository.updateBalance(sourceAccountId, amount.negate());
        accountRepository.updateBalance(destinationAccountId, amount);

        log.info("Transfer of {} from account {} to account {} completed successfully", amount, sourceAccountId, destinationAccountId);
    }

    private Transaction createTransaction(
            Account account, BigDecimal amount,
            TransactionType type, TransactionDirection direction,
            String description
    ) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .type(type)
                .direction(direction)
                .description(description)
                .build();
    }

    private void validateUser(Authentication connectedUser, Long accountId) {
        // ensure account belongs to this user
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        if (!Objects.equals(connectedUser.getName(), account.getUser().getEmail())) {
            log.error("Customer {} does not own account {} !", connectedUser.getPrincipal(), accountId);
            throw new RuntimeException("Customer does not own account " + accountId);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid transaction amount: {}", amount);
            throw new IllegalArgumentException("Transaction amount must be a positive integer");
        }
    }

    private void validateSufficientFunds(Account account, BigDecimal amount, String operation) {
        if (account.getBalance().compareTo(amount) < 0) {
            log.info("Insufficient funds for account {} to {} {}", account.getId(), operation, amount);
            throw new IllegalArgumentException("Insufficient funds for transaction. Account balance is: " + account.getBalance());
        }
    }

}
