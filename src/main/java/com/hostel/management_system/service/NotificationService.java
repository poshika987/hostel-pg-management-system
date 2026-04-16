package com.hostel.management_system.service;

import com.hostel.management_system.model.Notification;
import com.hostel.management_system.model.User;
import com.hostel.management_system.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void notify(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }
}
