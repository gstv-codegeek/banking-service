package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountCreationRequest;
import com.silentowl.banking_app.account.AccountMapper;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.exceptions.UserNotFoundException;
import com.silentowl.banking_app.kyc.KycVerificationStatus;
import com.silentowl.banking_app.role.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
            user.setDateOfBirth(LocalDate.of(2000, 12, 12));
            user.setEmail("admin@e-banking.com");
            user.setRole(Role.ROLE_ADMIN);
            user.setPassword(passwordEncoder.encode("admin"));

            user.setCreatedBy(1L);
            user.setCreatedDate(LocalDateTime.now());

            user.setStreet("Fedha Street");
            user.setCity("Nairobi");
            user.setState("Nairobi County");
            user.setCountry("Kenya");

            user.setAnnualIncome(BigDecimal.valueOf(200_000));
            user.setOccupation("Software Engineer");

            user.setKycStatus(KycVerificationStatus.VERIFIED);
            user.setCustomerTier(CustomerTier.SILVER);

            userRepository.save(user);
            log.info("Default admin created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }

    @Override
    @Transactional
    public User createUser(AccountCreationRequest accountRequest, CustomerTier customerTier, KycVerificationStatus kycVerificationStatus) {

        // extract user request
        UserRequest userRequest = accountRequest.getUserRequest();
        //save user if they do not already exist
        User user = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        if (user == null) {
            // create new user
            user = userMapper.mapToUserEntity(userRequest);
            user.setCustomerTier(customerTier);
            user.setKycStatus(kycVerificationStatus);
            user = userRepository.save(user);
        }

        // ensure user does not already have an account
        if (user.getAccount() != null) {
            throw new RuntimeException("User already have an account");
        }

        return user;
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
