package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import com.silentowl.banking_app.transaction.Transaction;
import com.silentowl.banking_app.user.User;
import com.silentowl.banking_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;


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
    public void sendNotification(User recipient, String message, DeliveryChannel channel, DeliveryStatus deliveryStatus) {

    }

//    private void validateParameters(Long recipientId, Long accountId, Long transactionId, NotificationType type, String message, DeliveryChannel channel) {
//        // check for null values
//        if (recipientId == null) throw new IllegalArgumentException("Message recipient must be provided!");
//        if (type == null) throw new IllegalArgumentException("Notification type must be provided");
//        if (message == null) throw new IllegalArgumentException("Message body cannot be empty");
//        if (channel == null) throw new IllegalArgumentException("Sending channel must be provided");
//
//        // ensure recipient exists
//        if (!userRepository.existsById(recipientId)) throw new UserNotFoundException("User not found with id " + recipientId);
//    }
}
