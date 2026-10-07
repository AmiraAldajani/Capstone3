package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.NotificationType;
import com.example.ejadwebapplication.Model.Notification;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Model.Staff;
import com.example.ejadwebapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Notification findNotificationById(Integer id);

    // بدون Id لأننا نرسل User كامل
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
    List<Notification> findAllByUserAndIsReadFalse(User user);
    Integer countByUserAndIsReadFalse(User user);

    List<Notification> findAllByStaffOrderByCreatedAtDesc(Staff staff);
    List<Notification> findAllByStaffAndIsReadFalse(Staff staff);
    Integer countByStaffAndIsReadFalse(Staff staff);

    // تُستخدم داخل @Transactional قبل حذف البلاغ أو الحساب
    void deleteAllByReport(Report report);
    void deleteAllByUser(User user);
    void deleteAllByStaff(Staff staff);

    // المستخدم يمسح الإشعارات المقروءة
    void deleteAllByUserAndIsReadTrue(User user);

    void deleteAllByStaffAndIsReadTrue(Staff staff);

    List<Notification> findAllByUserAndTypeOrderByCreatedAtDesc(User user, NotificationType type);
}