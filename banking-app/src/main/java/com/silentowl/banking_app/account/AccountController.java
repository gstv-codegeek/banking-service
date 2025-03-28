package com.silentowl.banking_app.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountOrchestrationService accountOrchestrationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountCreationResponse> createUserWithAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        AccountCreationResponse response = accountOrchestrationService.createAccountWithKycVerification(accountCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
