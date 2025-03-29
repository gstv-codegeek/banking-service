package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {

    void createNotification(User recipient, Account account, Transaction transaction, NotificationType type, String message, DeliveryChannel channel);
    void sendNotification(Notification notification);
    void markAsDelivered(Long notificationId);
    void markAsRead(Long notificationId);

    @Transactional
    void markAllAsRead(Account account);

    List<Notification> findUnreadNotificationsByAccount(Account account);

    @Transactional(readOnly = true)
    List<Notification> findAllNotificationsByAcount(Account account);
}
