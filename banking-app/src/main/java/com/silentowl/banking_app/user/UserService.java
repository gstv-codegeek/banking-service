package com.silentowl.banking_app.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    void createUser(UserRequest userRequest);
    void updateUser(Long userId, UserUpdateRequest user);
    List<UserResponse> findAllUsers(int page, int size);
    UserResponse findById(Long userId);
    void changePassword(Long userId, ChangePasswordRequest changePasswordRequest);
}
