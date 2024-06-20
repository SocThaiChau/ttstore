package com.example.back_end.repository;
import com.example.back_end.model.entity.Notification;
import com.example.back_end.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndIsReadFalse(User recipient);
    List<Notification> findByRecipient(User recipient);
    Notification getNotificationById(Long id);
}