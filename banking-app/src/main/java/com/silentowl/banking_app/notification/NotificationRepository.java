package com.silentowl.banking_app.notification;

import com.silentowl.banking_app.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAccountAndReadFalseOrderByCreatedDateDesc(Account account);

    List<Notification> findByAccountOrderByCreatedDateDesc(Account existingAccount);
}
