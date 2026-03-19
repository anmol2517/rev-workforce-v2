package com.revworkforce.controller;

import com.revworkforce.dto.NotificationDTO;
import com.revworkforce.service.NotificationService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieve notifications for a user with pagination")
    public ResponseEntity<ApiResponse> getUserNotifications(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Paginated notifications retrieved successfully")
                .data(notifications)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{userId}/all")
    @Operation(summary = "Get all user notifications", description = "Retrieve all notifications for a user")
    public ResponseEntity<ApiResponse> getAllUserNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All notifications retrieved successfully")
                .data(notifications)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{userId}/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications for a user")
    public ResponseEntity<ApiResponse> getUnreadCount(@PathVariable Long userId) {
        int count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Unread notification count retrieved")
                .data(count)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Notification marked as read")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/{userId}/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for a user")
    public ResponseEntity<ApiResponse> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All notifications marked as read")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}