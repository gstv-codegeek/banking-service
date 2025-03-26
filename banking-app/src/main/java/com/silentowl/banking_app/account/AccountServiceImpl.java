package com.silentowl.banking_app.account;

import com.silentowl.banking_app.kyc.KycVerificationRequest;
import com.silentowl.banking_app.user.UserMapper;
import com.silentowl.banking_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Override
    public AccountCreationResponse createAccountWithKycVerification(AccountCreationRequest request) {
        KycVerificationRequest kycRequest =
        return null;
    }

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
