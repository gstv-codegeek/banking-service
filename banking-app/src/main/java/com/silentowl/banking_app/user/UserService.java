package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.AccountCreationRequest;
import com.silentowl.banking_app.kyc.KycVerificationStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

//    void createUser(UserRequest userRequest);
    User createUser(AccountCreationRequest creationRequest, CustomerTier customerTier, KycVerificationStatus kycVerificationStatus);
    void updateUser(Long userId, UserUpdateRequest userUpdateRequest);
    List<UserResponse> findAllUsers(int page, int size);
    UserResponse findById(Long userId);
    void changePassword(Long userId, ChangePasswordRequest changePasswordRequest);
}
