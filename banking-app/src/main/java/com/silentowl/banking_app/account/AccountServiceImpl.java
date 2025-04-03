package com.silentowl.banking_app.account;

import com.silentowl.banking_app.exceptions.AccountCreationException;
import com.silentowl.banking_app.transaction.TransactionService;
import com.silentowl.banking_app.user.CustomerTier;
import com.silentowl.banking_app.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final TransactionService transactionService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private static final String BANK_CODE = "0063";
    private static final String BRANCH_CODE = "011";

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
        System.out.println(customer.getEmail());
        if (Objects.equals(customer.getEmail(), "admin@e-banking.com")){

            System.out.println(customer.getId());
            account.setCreatedBy(customer.getId());
        }
        account.setUser(customer);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setInitialDeposit(initialDeposit);
        account.setStatus(
                initialDeposit.compareTo(getMinimumDepositForAccountType(accountType)) >= 0
                ? AccountStatus.ACTIVE
                : AccountStatus.INACTIVE
        );
        account.setCurrency(String.valueOf(Currency.getInstance("KES").getCurrencyCode()));

        try {
            account = accountRepository.save(account);

            // Create initial transaction record
            boolean isNew = true;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            transactionService.processDeposit(account.getId(), initialDeposit, authentication, isNew);
            return account;
        } catch (Exception e) {
            log.error("Error creating account :", e);
            throw new AccountCreationException(customer.getId());
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
        };
    }
    private String generateUniqueAccountNumber() {
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return BRANCH_CODE + uniqueId + " " + BANK_CODE;
    }

    // ==== END ACCOUNT CREATION LOGIC ==== //


    @Override
    public void activateAccount(Long accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        existingAccount.setStatus(AccountStatus.ACTIVE);
    }


    @Override
    public void closeAccount(Long accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        existingAccount.setStatus(AccountStatus.CLOSED);
    }


    @Override
    public void freezeAccount(Long accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        existingAccount.setStatus(AccountStatus.FROZEN);
    }


    @Override
    public List<AccountCreationResponse> findAllAccounts() {
        return accountRepository.findAll().stream().map(accountMapper::mapToAccountResponse).toList();
    }


    @Override
    public List<AccountCreationResponse> findAccountById(Long accountId) {
        return accountRepository.findById(accountId).stream().map(accountMapper::mapToAccountResponse).toList();
    }

}
