package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Notification findNotificationById(Integer id);

    // User
    //List<Notification> findAllByUserIdOrderByCreatedAtDesc(Integer userId);
    //List<Notification> findAllByUserIdAndIsReadFalse(Integer userId);
    //Integer countByUserIdAndIsReadFalse(Integer userId);

    // Staff
    //List<Notification> findAllByStaffIdOrderByCreatedAtDesc(Integer staffId);
    //List<Notification> findAllByStaffIdAndIsReadFalse(Integer staffId);
    //Integer countByStaffIdAndIsReadFalse(Integer staffId);

}
