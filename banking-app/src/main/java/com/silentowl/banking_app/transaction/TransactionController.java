package com.silentowl.banking_app.transaction;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/withdraw/account/{account-id}/amount/{amount}")
    public ResponseEntity<Void> processWithdrawal(@PathVariable("account-id") Long accountId, @PathVariable("amount") BigDecimal amount, Authentication connectedUser) {
        transactionService.processWithdrawal(accountId, amount, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/transfer/source/{source-account-id}/destination/{destination-account-id}/amount/{amount}")
    public ResponseEntity<Void> processTransfer(@Valid @PathVariable("source-account-id") Long sourceAccountId,
                                                @Valid @PathVariable("destination-account-id") Long destinationAccountId,
                                                @Valid @PathVariable("amount") BigDecimal amount, Authentication connectedUser) {
        transactionService.processTransfer(sourceAccountId, destinationAccountId, amount, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
