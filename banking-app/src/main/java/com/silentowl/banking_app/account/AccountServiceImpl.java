package com.silentowl.banking_app.account;

import com.silentowl.banking_app.exceptions.UserNotFoundException;
import com.silentowl.banking_app.role.Role;
import com.silentowl.banking_app.user.User;
import com.silentowl.banking_app.user.UserMapper;
import com.silentowl.banking_app.user.UserRepository;
import com.silentowl.banking_app.user.UserRequest;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void lockAccount(Long accountId) {

    }

    @Override
    public void unlockAccount(Long accountId) {

    }

    @Override
    public Optional<List<AccountResponse>> findAllAccounts() {
        return Optional.empty();
    }

    @Override
    public Optional<AccountResponse> findAccountById(Long accountId) {
        return Optional.empty();
    }
}
