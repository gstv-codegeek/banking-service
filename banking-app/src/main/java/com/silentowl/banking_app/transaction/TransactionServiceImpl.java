package com.silentowl.banking_app.transaction;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.account.AccountStatus;
import com.silentowl.banking_app.exceptions.InsufficientFundsException;
import com.silentowl.banking_app.notification.DeliveryChannel;
import com.silentowl.banking_app.notification.Notification;
import com.silentowl.banking_app.notification.NotificationService;
import com.silentowl.banking_app.notification.NotificationType;
import com.silentowl.banking_app.user.User;
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
    private final NotificationService notificationService;


    @Transactional
    public void processDeposit(Long accountId, BigDecimal amount, Authentication connectedUser) {

        // Lock account to prevent race conditions
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> {
                    log.error("Deposit failed: Account {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        // prevent deposits if account is the closed
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.error("Deposit is not allowed on this account. Account is {}", account.getStatus());
            throw new IllegalArgumentException("Account is Closed! Transactions are not allowed.");
        }


        log.info("Processing deposit of {} to account {}", amount, accountId);
        validateUser(connectedUser, accountId); // ensure user owns account
        validateAmount(amount);

        // create & save credit transaction
        Transaction creditTx = createTransaction(
                account, amount, TransactionType.DEPOSIT,
                TransactionDirection.CREDIT, "Deposit Received"
        );
        transactionRepository.save(creditTx);

        // atomic balance update
        accountRepository.updateBalance(accountId, amount);
        log.info("Deposit of {} to account {} completed successfully", amount, accountId);

        // create notification
        notificationService.createNotification(
                account.getUser(), account, creditTx, NotificationType.DEPOSIT_CONFIRMATION,
                "Dear " + account.getUser().getFirstName().toUpperCase() +
                        ", Your deposit of " + amount + " to your account " + account.getIban() +
                        " has been posted successfully",
                DeliveryChannel.EMAIL
        );

        log.info("Notification created for funds deposit");
    }


    @Transactional
    public void processWithdrawal(Long accountId, BigDecimal amount, Authentication connectedUser) {
        // Lock account to prevent race conditions
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> {
                    log.error("Withdrawal failed: Account {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        // prevent withdrawals if account is the closed | frozen | inactive
        if (account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN ||
                account.getStatus() == AccountStatus.INACTIVE) {
            log.error("Withdrawal is not allowed. Your account is {}", account.getStatus());
            throw new IllegalArgumentException("Withdrawal is not allowed! Your account is " + account.getStatus());
        }

        log.info("Processing withdraw of {} from account {}", amount, accountId);
        validateUser(connectedUser, accountId); // ensure user owns account
        validateAmount(amount);
        validateSufficientFunds(account, amount, "withdraw");

        // debit transaction for withdrawing account
        Transaction debitTx = createTransaction(
                account, amount, TransactionType.WITHDRAWAL,
                TransactionDirection.DEBIT, "Withdrawal");
        transactionRepository.save(debitTx);

        // atomic balance update
        accountRepository.updateBalance(accountId, amount.negate());
        log.info("Withdrawal of {} from account {} completed successfully", amount, accountId);

        // create notification
        notificationService.createNotification(
                account.getUser(), account, debitTx, NotificationType.WITHDRAWAL_CONFIRMATION,
                "Dear " + account.getUser().getFirstName().toUpperCase() +
                        ", Your withdrawal of " + amount + " from your account " + account.getIban() + " has been processed successfully.",
                DeliveryChannel.EMAIL
        );
        log.info("Notification created for funds withdrawal");
    }


    @Transactional
    public void processTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount, Authentication connectedUser) {
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

        // prevent transfers if source account is the closed, frozen or inactive
        if (sourceAccount.getStatus() == AccountStatus.CLOSED ||
                sourceAccount.getStatus() == AccountStatus.FROZEN ||
                sourceAccount.getStatus() == AccountStatus.INACTIVE){
            log.error("Transfer is not allowed for this account! Your account is {}", sourceAccount.getStatus());
            throw new IllegalArgumentException("Transfer is not allowed! Your account is " + sourceAccount.getStatus());
        }

        // prevent transfers if destination account is the closed
        if (destinationAccount.getStatus() == AccountStatus.CLOSED){
            log.error("Transfer is not allowed for this account! Your account is {}", sourceAccount.getStatus());
            throw new IllegalArgumentException("Transfer is not allowed! The destination account is " + destinationAccount.getStatus());
        }


        log.info("Processing transfer of {} from account {} to account {}", amount, sourceAccountId, destinationAccountId);
        validateUser(connectedUser, sourceAccountId);
        validateAmount(amount);
        validateSufficientFunds(sourceAccount, amount, "transfer");

        // create transactions
        Transaction debitTx = createTransaction(
                sourceAccount, amount, TransactionType.TRANSFER_SENT,
                TransactionDirection.DEBIT, "Transfer to " + destinationAccount.getIban());
        Transaction creditTx = createTransaction(
                destinationAccount, amount, TransactionType.TRANSFER_RECEIVED,
                TransactionDirection.CREDIT, "Transfer from " + sourceAccount.getIban());

        // save both transactions
        List<Transaction> savedTxs = transactionRepository.saveAll(List.of(debitTx, creditTx));
        Transaction savedDebitTx = savedTxs.getFirst();
        Transaction savedCreditTx = savedTxs.getLast();

        // atomic account balance updates
        accountRepository.updateBalance(sourceAccountId, amount.negate());
        accountRepository.updateBalance(destinationAccountId, amount);
        log.info("Transfer of {} from account {} to account {} completed successfully", amount, sourceAccountId, destinationAccountId);

        // create notification for sender
        notificationService.createNotification(
                sourceAccount.getUser(), sourceAccount, debitTx, NotificationType.TRANSFER_SENT,
                "You have successfully transferred " + amount +
                        " to " + destinationAccount.getUser().getFirstName() + "-" + destinationAccount.getUser().getPhoneNumber() +
                        " at " + savedCreditTx.getCreatedDate(),
                DeliveryChannel.EMAIL
        );
        // create notification for recipient
        notificationService.createNotification(
                destinationAccount.getUser(), destinationAccount, creditTx, NotificationType.TRANSFER_RECEIVED,
                "Dear " + destinationAccount.getUser().getFirstName().toUpperCase() + ", " +
                        "You have received " + destinationAccount.getCurrency() + " " + amount +
                        "on your account " + destinationAccount.getIban() +
                        " from " + sourceAccount.getUser().getFirstName().toUpperCase() + "-" + sourceAccount.getUser().getPhoneNumber() +
                        " at " + savedCreditTx.getCreatedDate(),
                DeliveryChannel.EMAIL
        );
        log.info("Notification created for funds transfer");
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
