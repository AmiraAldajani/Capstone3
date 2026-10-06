package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.Entity.Notification;
import com.example.ejadwebapplication.Service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<?> addNotification(@RequestBody @Valid Notification notification) {
        notificationService.addNotification(notification);
        return ResponseEntity.status(200).body(new ApiResponse("Notification added successfully"));
    }

    @PutMapping("/read/{userId}/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Integer userId, @PathVariable Integer notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.status(200).body(new ApiResponse("Notification marked as read"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.status(200).body(new ApiResponse("Notification deleted successfully"));
    }
}
