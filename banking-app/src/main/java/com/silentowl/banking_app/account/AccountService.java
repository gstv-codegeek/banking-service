package com.silentowl.banking_app.account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    AccountCreationResponse createAccountWithKycVerification(AccountCreationRequest request);
    void lockAccount(Long accountId);
    void unlockAccount(Long accountId);
    Optional<List<AccountCreationResponse>> findAllAccounts();
    Optional<AccountCreationResponse> findAccountById(Long accountId);

}
