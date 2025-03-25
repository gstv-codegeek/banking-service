package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountCreationRequest;
import com.silentowl.banking_app.account.AccountMapper;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.exceptions.UserNotFoundException;
import com.silentowl.banking_app.role.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
//    private final AccountCreationRequest accountRequest;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    // create an admin user
    @PostConstruct
    public void createAdmin() {
        if (!userRepository.existsByRole(Role.ROLE_ADMIN)) {
            User user = new User();
            user.setFirstName("SYSTEM");
            user.setLastName("ADMIN");
            user.setEmail("admin@e-banking.com");
            user.setRole(Role.ROLE_ADMIN);
            user.setPassword(passwordEncoder.encode("admin"));

            user.setCreatedBy(1L);
            user.setCreatedDate(LocalDateTime.now());

            user.setStreet("Fedha Street");
            user.setCity("Nairobi");
            user.setState("Nairobi County");
            user.setCountry("Kenya");

            userRepository.save(user);
            log.info("Default admin created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }

//    @Override
//    public void createUser(UserRequest userRequest) {
//        if (userRepository.existsByEmail(userRequest.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//        User user = userMapper.mapToUserEntity(userRequest);
//        userRepository.save(user);
//    }

    @Override
    @Transactional
    public void createUserWithAccount(AccountCreationRequest accountRequest) {
        System.out.println(accountRequest.getUserRequest());
        // extract user request
        UserRequest userRequest = accountRequest.getUserRequest();
        //check if user already exists
        User user = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        if (user == null) {
            // create new user
            user = userMapper.mapToUserEntity(userRequest);
            user = userRepository.save(user);
        }

        // ensure user does not already have an account
        if (user.getAccount() != null) {
            throw new RuntimeException("User already have an account");
        }

        // generate a unique iban
        final String iban = generateIban();
        //create account and associate it with user
        Account account = accountMapper.mapToAccountEntity(iban, user);
        accountRepository.save(account);

//        user.setAccount(account);
//        userRepository.save(user);
    }
    private String generateIban() {
        String newIban;
        do {
            newIban = Iban.random().getAccountNumber();
        } while (accountRepository.existsByIban(newIban));
        return newIban;
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        System.out.println(userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found with id:" + userId));
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> findAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(userMapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long userId) {
        return userRepository.findById(userId).map(userMapper::mapToUserResponse).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + userId));

        //check current password
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    }
}
