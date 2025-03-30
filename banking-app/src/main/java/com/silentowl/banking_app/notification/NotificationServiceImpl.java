package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.account.AccountRepository;
import com.silentowl.banking_app.exceptions.NotificationNotFoundException;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public void createNotification(User recipient, Account account, Transaction transaction, NotificationType type, String message, DeliveryChannel channel) {
        try {
            Notification notification = Notification.builder()
                    .recipient(recipient)
                    .account(account)
                    .transaction(transaction)
                    .type(type)
                    .message(message)
                    .isRead(false)
                    .deliveryStatus(DeliveryStatus.PENDING)
                    .deliveryChannel(channel)
                    .build();

            notificationRepository.save(notification);
            log.info("Notification created for user {}", recipient.getId());
        } catch (Exception e) {
            log.error("Failed to create notification for user {}: {}", recipient.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create notification" + e);
        }

    }


    @Override
    @Transactional
    public void sendNotification(Notification notification) {
        notification.setDeliveryStatus(DeliveryStatus.SENT);
        notificationRepository.save(notification);
    }


    @Override
    @Transactional
    public void markAsDelivered(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        notification.setDeliveryStatus(DeliveryStatus.DELIVERED);
        notificationRepository.save(notification);
    }


    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    @Override
    public void markAllAsRead(Account account) {
        Account existingAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + account.getId()));
        List<Notification> unreadNotifications = findUnreadNotificationsByAccount(existingAccount);

        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Notification> findUnreadNotificationsByAccount(Account account) {
        Account existingAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + account.getId()));
        return notificationRepository.findByAccountAndIsReadFalseOrderByCreatedDateDesc(existingAccount);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Notification> findAllNotificationsByAcount(Account account) {
        Account existingAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + account.getId()));
        return notificationRepository.findByAccountOrderByCreatedDateDesc(existingAccount);
    }

}
