package com.silentowl.banking_app.user;

import com.silentowl.banking_app.exceptions.UserNotFoundException;
import com.silentowl.banking_app.role.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // create an admin user
    @PostConstruct
    public void createAdmin() {
        if (!userRepository.existsByRole(Role.ROLE_ADMIN)) {
            User user = new User();
            user.setFirstName("SYSTEM");
            user.setLastName("ADMIN");
            user.setEmail("admin@e-banking.com");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Role.ROLE_ADMIN);
            user.setCreatedBy(1L);
            user.setCreatedDate(LocalDateTime.now());
            userRepository.save(user);

            log.info("Default admin created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }

    @Override
    public void createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = userMapper.mapToUserEntity(userRequest);
        userRepository.save(user);
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
