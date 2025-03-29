package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.AccountCreationRequest;
import com.silentowl.banking_app.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User mapToUserEntity(AccountCreationRequest accountRequest) {
        return User.builder()
                .firstName(accountRequest.getUserRequest().getFirstName())
                .lastName(accountRequest.getUserRequest().getLastName())
                .dateOfBirth(accountRequest.getUserRequest().getDateOfBirth())
                .email(accountRequest.getUserRequest().getEmail())
                .phoneNumber((accountRequest.getUserRequest().getPhoneNumber()))
                .password(passwordEncoder.encode(accountRequest.getUserRequest().getPassword()))
                .city(accountRequest.getUserRequest().getCity())
                .street(accountRequest.getUserRequest().getStreet())
                .state(accountRequest.getUserRequest().getState())
                .country(accountRequest.getUserRequest().getCountry())
                .annualIncome(accountRequest.getUserRequest().getAnnualIncome())
                .occupation(accountRequest.getUserRequest().getOccupation())
                .role(Role.ROLE_CUSTOMER)
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .street(user.getStreet())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .annualIncome(user.getAnnualIncome())
                .occupation(user.getOccupation())
                .customerTier(user.getCustomerTier())
                .kycStatus(user.getKycStatus())
                .build();
    }
}
