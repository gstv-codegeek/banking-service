package com.silentowl.banking_app.account;

import com.silentowl.banking_app.common.AbstractEntity;
import com.silentowl.banking_app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String iban;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String accountType;
    private String currency;
    private BigDecimal balance;
    private boolean locked;
}
