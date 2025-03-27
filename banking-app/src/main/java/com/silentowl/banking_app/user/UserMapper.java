package com.silentowl.banking_app.user;

import com.silentowl.banking_app.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User mapToUserEntity(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .dateOfBirth(userRequest.getDateOfBirth())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .city(userRequest.getCity())
                .street(userRequest.getStreet())
                .state(userRequest.getState())
                .country(userRequest.getCountry())
                .annualIncome(userRequest.getAnnualIncome())
                .occupation(userRequest.getOccupation())
                .role(Role.ROLE_CUSTOMER)
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
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
