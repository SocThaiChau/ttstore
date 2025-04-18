package com.example.back_end.service.impl;

import com.example.back_end.model.entity.Notification;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByRecipientAndIsReadFalse(user);
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByRecipient(user);
    }

    public void markNotificationAsRead(Notification notification) {
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(User user) {
        List<Notification> notifications = getNotificationsByUser(user);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }


    public void createLikeNotification(User liker, Product product) {
        Notification notification = new Notification();
        notification.setRecipient(product.getUser());
        notification.setMessage(liker.getName() + " đã thích sản phẩm " + product.getName() + ".");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public void createFollowNotification(User follower, User followed) {
        Notification notification = new Notification();
        notification.setRecipient(followed);
        notification.setMessage(follower.getName() + " đã theo dõi bạn.");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }


}