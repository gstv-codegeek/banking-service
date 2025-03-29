package com.silentowl.banking_app.account;

import com.silentowl.banking_app.transaction.DepositRequest;
import com.silentowl.banking_app.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountOrchestrationService accountOrchestrationService;
    private final TransactionService transactionService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountCreationResponse> createUserWithAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        AccountCreationResponse response = accountOrchestrationService.createAccountWithKycVerification(accountCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{account-id}/deposit")
    public ResponseEntity<String> processDeposit(@PathVariable("account-id") Long accountId, @RequestBody DepositRequest depositRequest, Authentication connectedUser) {
        String username = connectedUser.getName();
        log.info("User {} is attempting to deposit {} into account {}", username, depositRequest.getAmount(), accountId);
        transactionService.processDeposit(accountId, depositRequest.getAmount(), connectedUser);
        return ResponseEntity.ok("Deposit of " + depositRequest.getAmount() + " to account " + accountId + " successful.");
    }
}
