package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Entity.Notification;
import com.example.ejadwebapplication.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {

        // how do we handle if list was empty ?

        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Integer id) {
        Notification notification = notificationRepository.findNotificationById(id);

        if (notification == null) {
            throw new ApiException("Notification not found with ID: " + id);
        }
        return notification;
    }

    public void addNotification(Notification notification) {
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    // this method should be deleted because it doesnt make sense to update a notification body and type
    /*public void updateNotification(Integer id, Notification notification) {
        Notification oldNotification = notificationRepository.findNotificationById(id);

        if (oldNotification == null) {
            throw new ApiException("Notification not found with ID:  " + id);
        }
        oldNotification.setType(notification.getType());
        oldNotification.setMessage(notification.getMessage());
        oldNotification.setIsRead(notification.getIsRead());

        notificationRepository.save(oldNotification);
    }*/

    public void markAsRead(Integer userId, Integer notificationId) {
        Notification notification = getNotificationById(notificationId);

//            we add this after the user class
//        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
//            throw new ApiException("This notification does not belong to this user");
//        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Integer id) {
        Notification notification = getNotificationById(id);

        notificationRepository.delete(notification);
    }


}
