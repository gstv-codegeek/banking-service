package com.silentowl.banking_app.user;

import com.silentowl.banking_app.account.AccountCreationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void createUser(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        userService.createUserWithAccount(accountCreationRequest);
    }

    @PutMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUser(
            @PathVariable("user-id") Long userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        userService.updateUser(userId, userUpdateRequest);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        List<UserResponse> users = userService.findAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> findById(@Valid @PathVariable("user-id") Long userId) {
        UserResponse user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            Authentication connectedUser
    ) {
        final long userId = ((User) connectedUser.getPrincipal()).getId();
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
