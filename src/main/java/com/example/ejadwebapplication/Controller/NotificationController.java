package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// ما فيه add: الإشعارات تنشأ داخلياً من ReportService و ReportMatchService
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllNotifications() {
        return ResponseEntity.status(200).body(notificationService.getAllNotifications());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(notificationService.getNotificationById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.status(200).body(new ApiResponse("Notification deleted successfully"));
    }

    // ================= User =================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUserUnreadNotifications(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(notificationService.getUserUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<?> countUserUnread(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(Map.of("unreadCount", notificationService.countUserUnread(userId)));
    }

    @PutMapping("/user/{userId}/read/{notificationId}")
    public ResponseEntity<?> markAsReadByUser(@PathVariable Integer userId, @PathVariable Integer notificationId) {
        notificationService.markAsReadByUser(userId, notificationId);
        return ResponseEntity.status(200).body(new ApiResponse("Notification marked as read"));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsReadByUser(@PathVariable Integer userId) {
        notificationService.markAllAsReadByUser(userId);
        return ResponseEntity.status(200).body(new ApiResponse("All notifications marked as read"));
    }

    @DeleteMapping("/user/{userId}/delete-read")
    public ResponseEntity<?> deleteReadByUser(@PathVariable Integer userId) {
        notificationService.deleteReadByUser(userId);
        return ResponseEntity.status(200).body(new ApiResponse("Read notifications deleted successfully"));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getUserNotificationsByType(@PathVariable Integer userId, @PathVariable String type) {
        return ResponseEntity.status(200).body(notificationService.getUserNotificationsByType(userId, type));
    }

    // ================= Staff =================

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<?> getStaffNotifications(@PathVariable Integer staffId) {
        return ResponseEntity.status(200).body(notificationService.getStaffNotifications(staffId));
    }

    @GetMapping("/staff/{staffId}/unread")
    public ResponseEntity<?> getStaffUnreadNotifications(@PathVariable Integer staffId) {
        return ResponseEntity.status(200).body(notificationService.getStaffUnreadNotifications(staffId));
    }

    @GetMapping("/staff/{staffId}/unread-count")
    public ResponseEntity<?> countStaffUnread(@PathVariable Integer staffId) {
        return ResponseEntity.status(200).body(Map.of("unreadCount", notificationService.countStaffUnread(staffId)));
    }

    @PutMapping("/staff/{staffId}/read/{notificationId}")
    public ResponseEntity<?> markAsReadByStaff(@PathVariable Integer staffId, @PathVariable Integer notificationId) {
        notificationService.markAsReadByStaff(staffId, notificationId);
        return ResponseEntity.status(200).body(new ApiResponse("Notification marked as read"));
    }

    @PutMapping("/staff/{staffId}/read-all")
    public ResponseEntity<?> markAllAsReadByStaff(@PathVariable Integer staffId) {
        notificationService.markAllAsReadByStaff(staffId);
        return ResponseEntity.status(200).body(new ApiResponse("All notifications marked as read"));
    }

    @DeleteMapping("/staff/{staffId}/delete-read")
    public ResponseEntity<?> deleteReadByStaff(@PathVariable Integer staffId) {
        notificationService.deleteReadByStaff(staffId);
        return ResponseEntity.status(200).body(new ApiResponse("Read notifications deleted successfully"));
    }
}