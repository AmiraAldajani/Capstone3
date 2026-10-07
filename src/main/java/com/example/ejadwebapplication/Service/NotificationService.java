package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOOUT.NotificationDTOOut;
import com.example.ejadwebapplication.Enums.NotificationType;
import com.example.ejadwebapplication.Model.*;
import com.example.ejadwebapplication.Repository.NotificationRepository;
import com.example.ejadwebapplication.Repository.StaffRepository;
import com.example.ejadwebapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    // القائمة الفاضية ترجع [] عادي، ما تحتاج exception
    public List<NotificationDTOOut> getAllNotifications() {
        return convertListToDTO(notificationRepository.findAll());
    }

    public NotificationDTOOut getNotificationById(Integer id) {
        return convertToDTO(findNotification(id));
    }

    public void deleteNotification(Integer id) {
        notificationRepository.delete(findNotification(id));
    }

    // ================= User =================

    public List<NotificationDTOOut> getUserNotifications(Integer userId) {
        return convertListToDTO(notificationRepository.findAllByUserOrderByCreatedAtDesc(findUser(userId)));
    }

    public List<NotificationDTOOut> getUserUnreadNotifications(Integer userId) {
        return convertListToDTO(notificationRepository.findAllByUserAndIsReadFalse(findUser(userId)));
    }

    public Integer countUserUnread(Integer userId) {
        return notificationRepository.countByUserAndIsReadFalse(findUser(userId));
    }

    // نتأكد إن الإشعار يخص هذا المستخدم قبل ما نعدّله
    public void markAsReadByUser(Integer userId, Integer notificationId) {
        User user = findUser(userId);
        Notification notification = findNotification(notificationId);
        if (notification.getUser() == null || !notification.getUser().getId().equals(user.getId())) {
            throw new ApiException("This notification does not belong to this user");
        }
        markAsRead(notification);
    }

    public void markAllAsReadByUser(Integer userId) {
        List<Notification> unread = notificationRepository.findAllByUserAndIsReadFalse(findUser(userId));
        for (Notification notification : unread) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    // derived delete لازم يكون داخل transaction
    @Transactional
    public void deleteReadByUser(Integer userId) {
        notificationRepository.deleteAllByUserAndIsReadTrue(findUser(userId));
    }

    public List<NotificationDTOOut> getUserNotificationsByType(Integer userId, String type) {
        NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Type must be NEW_REPORT, MATCH_FOUND or MATCH_CONFIRMED");
        }
        return convertListToDTO(notificationRepository
                .findAllByUserAndTypeOrderByCreatedAtDesc(findUser(userId), notificationType));
    }

    // ================= Staff =================

    public List<NotificationDTOOut> getStaffNotifications(Integer staffId) {
        return convertListToDTO(notificationRepository.findAllByStaffOrderByCreatedAtDesc(findStaff(staffId)));
    }

    public List<NotificationDTOOut> getStaffUnreadNotifications(Integer staffId) {
        return convertListToDTO(notificationRepository.findAllByStaffAndIsReadFalse(findStaff(staffId)));
    }


    public Integer countStaffUnread(Integer staffId) {
        return notificationRepository.countByStaffAndIsReadFalse(findStaff(staffId));
    }

    public void markAsReadByStaff(Integer staffId, Integer notificationId) {
        Staff staff = findStaff(staffId);
        Notification notification = findNotification(notificationId);
        if (notification.getStaff() == null || !notification.getStaff().getId().equals(staff.getId())) {
            throw new ApiException("This notification does not belong to this staff member");
        }
        markAsRead(notification);
    }

    public void markAllAsReadByStaff(Integer staffId) {
        List<Notification> unread = notificationRepository.findAllByStaffAndIsReadFalse(findStaff(staffId));
        for (Notification notification : unread) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void deleteReadByStaff(Integer staffId) {
        notificationRepository.deleteAllByStaffAndIsReadTrue(findStaff(staffId));
    }

    // ================= للاستخدام الداخلي (من ReportService و ReportMatchService) =================

    // إشعار لكل موظف موثّق في أماكن البلاغ
    public void notifyStaffAboutNewReport(Report report) {
        notifyStaffAtLocations(report, report.getLocations());
    }

    // تُستخدم من الإضافة والتعديل: عند التعديل نرسل الأماكن اللي انضافت بس
    public void notifyStaffAtLocations(Report report, Set<Location> locations) {
        for (Location location : locations) {
            for (Staff staff : staffRepository.findAllByLocationAndIsVerifiedTrue(location)) {
                // الموظف اللي رفع البلاغ بنفسه ما يحتاج إشعار
                if (report.getStaff() != null && report.getStaff().getId().equals(staff.getId())) {
                    continue;
                }
                Notification notification = new Notification();
                notification.setType(NotificationType.NEW_REPORT);
                notification.setMessage(limit("New " + report.getType() + " report at "
                        + location.getName() + ": " + report.getTitle()));
                notification.setStaff(staff);
                notification.setReport(report);
                save(notification);
            }
        }
    }

    // إشعار لصاحب البلاغ، سواء كان user أو staff
    public void notifyReportOwner(Report report, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(limit(message));
        notification.setReport(report);
        if (report.getUser() != null) {
            notification.setUser(report.getUser());
        } else {
            notification.setStaff(report.getStaff());
        }
        save(notification);
    }

    // ================= Helpers =================

    private void save(Notification notification) {
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    private void markAsRead(Notification notification) {
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // الرسالة في الجدول حدها 300 حرف
    private String limit(String message) {
        return message.length() > 300 ? message.substring(0, 300) : message;
    }

    private Notification findNotification(Integer id) {
        Notification notification = notificationRepository.findNotificationById(id);
        if (notification == null) {
            throw new ApiException("Notification not found with ID: " + id);
        }
        return notification;
    }

    private User findUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }
        return user;
    }

    private Staff findStaff(Integer id) {
        Staff staff = staffRepository.findStaffById(id);
        if (staff == null) {
            throw new ApiException("Staff not found");
        }
        return staff;
    }

    private List<NotificationDTOOut> convertListToDTO(List<Notification> notifications) {
        List<NotificationDTOOut> result = new ArrayList<>();
        for (Notification notification : notifications) {
            result.add(convertToDTO(notification));
        }
        return result;
    }

    private NotificationDTOOut convertToDTO(Notification notification) {
        Integer userId = notification.getUser() != null ? notification.getUser().getId() : null;
        Integer staffId = notification.getStaff() != null ? notification.getStaff().getId() : null;
        return new NotificationDTOOut(notification.getId(), notification.getType().name(),
                notification.getMessage(), notification.getIsRead(), notification.getCreatedAt(),
                userId, staffId,
                notification.getReport().getId(), notification.getReport().getTitle());
    }
}