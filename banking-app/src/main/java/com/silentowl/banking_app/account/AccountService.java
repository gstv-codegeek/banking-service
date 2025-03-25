package com.silentowl.banking_app.account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    void lockAccount(Long accountId);
    void unlockAccount(Long accountId);
    Optional<List<AccountResponse>> findAllAccounts();
    Optional<AccountResponse> findAccountById(Long accountId);

}
