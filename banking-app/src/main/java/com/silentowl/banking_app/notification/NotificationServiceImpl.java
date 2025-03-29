package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;


    @Override
    public void postNotification(User recipient, Account account, Transaction transaction, NotificationType type, String message, DeliveryChannel channel) {

    }

    @Override
    public void sendNotification(User recipient, String message, DeliveryChannel channel, DeliveryStatus deliveryStatus) {

    }
}
