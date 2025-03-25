package com.silentowl.banking_app.account;

import com.silentowl.banking_app.user.User;
import com.silentowl.banking_app.user.UserRequest;
import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreationRequest {
    @Valid
    private UserRequest userRequest;
}
