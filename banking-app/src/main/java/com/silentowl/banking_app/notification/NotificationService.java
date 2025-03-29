package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;

public interface NotificationService {

    void createNotification(User recipient, Account account, Transaction transaction, NotificationType type, String message, DeliveryChannel channel);
    void sendNotification(User recipient, String message, DeliveryChannel channel, DeliveryStatus deliveryStatus);
}
