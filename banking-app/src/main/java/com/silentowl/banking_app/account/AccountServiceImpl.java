package com.silentowl.banking_app.account;

import com.silentowl.banking_app.exceptions.AccountCreationException;
import com.silentowl.banking_app.exceptions.TransactionException;
import com.silentowl.banking_app.exceptions.UserNotFoundException;
import com.silentowl.banking_app.kyc.KycVerificationRequest;
import com.silentowl.banking_app.kyc.KycVerificationResponse;
import com.silentowl.banking_app.kyc.KycVerificationService;
import com.silentowl.banking_app.kyc.KycVerificationStatus;
import com.silentowl.banking_app.user.*;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Account createAccount(User customer, CustomerTier customerTier, BigDecimal initialDeposit, AccountType accountType) {

        // 1. Validate Create account inputs
        validateCreateAccountInput(customer, customerTier, initialDeposit);

        // 2. Determine initial deposit
        validateInitialDeposit(initialDeposit, accountType);

        // 3. Generate account number
        String accountNumber = generateUniqueAccountNumber();

        // 4. Create account
        System.out.println(accountNumber + " - " + customer.getFirstName() + " - " + accountType + " - " + initialDeposit);
        Account account = new Account();
        account.setIban(accountNumber);
        account.setUser(customer);
        account.setAccountType(accountType);
        account.setBalance(initialDeposit);
        account.setInitialDeposit(initialDeposit);
        account.setStatus(
                initialDeposit.compareTo(getMinimumDepositForAccountType(accountType)) >= 0
                ? AccountStatus.ACTIVE
                : AccountStatus.INACTIVE
        );
        account.setCurrency(String.valueOf(Currency.getInstance("KES").getCurrencyCode()));
//        account.setCurrency("KES");
        try {
            account = accountRepository.save(account);

            return account;
            // Create initial transaction record
//            createInitialDepositTransaction(account, initialDeposit);
        } catch (Exception e) {
            throw new AccountCreationException("Failed to create account");
        }
    }

    private void validateCreateAccountInput(User customer, CustomerTier customerTier, BigDecimal initialDeposit) {
        if (customer == null) throw new IllegalArgumentException("Customer not cannot be null");
        if (customerTier == null) throw new IllegalArgumentException("Customer tier cannot be null");
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Initial deposit must not be non-negative");
    }

    private void validateInitialDeposit(BigDecimal initialDeposit, AccountType accountType) {
        BigDecimal minimumDeposit = getMinimumDepositForAccountType(accountType);
        if (initialDeposit.compareTo(minimumDeposit) < 0) {
            throw new IllegalArgumentException(
                    "Minimum initial deposit required for opening a " + accountType + " ACCOUNT is " + minimumDeposit
            );
        }
    }
    private BigDecimal getMinimumDepositForAccountType(AccountType accountType) {
        return switch (accountType) {
            case ELITE -> BigDecimal.valueOf(10000.00);
            case PREMIUM -> BigDecimal.valueOf(5000.00);
            case STANDARD -> BigDecimal.valueOf(500.00);
            default -> throw new IllegalArgumentException("Unsupported account type");
        };
    }
    private String generateUniqueAccountNumber() {
        String newIban;
        do newIban = String.valueOf(new Iban.Builder().countryCode(CountryCode.KE).bankCode("011").branchCode("063").buildRandom());
            while (accountRepository.existsByIban(newIban));

        return newIban;
    }
//    private void createInitialDepositTransaction(Account account, BigDecimal initialDeposit) {
//        Transaction initialTransaction = new Transaction();
//        initialTransaction.setAccount(account);
//        initialTransaction.setAmount(initialDeposit);
//        initialTransaction.setType(TransactionType.DEPOSIT);
//        initialTransaction.setDescription("Initial Account Deposit");
//        initialTransaction.setTimestamp(LocalDateTime.now());
//
//        transactionRepository.save(initialTransaction);
//    }
    // ==== END ACCOUNT CREATION LOGIC ==== //


    @Override
    public void lockAccount(Long accountId) {

    }


    @Override
    public void unlockAccount(Long accountId) {

    }


    @Override
    public Optional<List<AccountCreationResponse>> findAllAccounts() {
        return Optional.empty();
    }


    @Override
    public Optional<AccountCreationResponse> findAccountById(Long accountId) {
        return Optional.empty();
    }
}
