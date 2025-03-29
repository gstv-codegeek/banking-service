package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.common.AbstractEntity;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "notifications")
public class Notification extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead;
    private LocalDateTime readAt;

    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryChannel deliveryChannel;

    @PrePersist
    public void onCreate() {
        deliveryStatus = DeliveryStatus.PENDING;
        isRead = false;
    }
}
