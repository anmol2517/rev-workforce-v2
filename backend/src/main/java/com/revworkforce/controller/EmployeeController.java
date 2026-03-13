package com.revworkforce.controller;

import com.revworkforce.dto.*;
import com.revworkforce.service.*;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
@Tag(name = "Employee Management", description = "Employee-only endpoints for self-service operations")
public class EmployeeController {

    private final UserService userService;
    private final LeaveApplicationService leaveApplicationService;
    private final HolidayService holidayService;
    private final PerformanceReviewService performanceReviewService;
    private final EmployeeGoalService employeeGoalService;
    private final AnnouncementService announcementService;
    private final NotificationService notificationService;
    private final ReportService reportService;
    private final QRCodeService qrCodeService;
    private final LeaveBalanceService leaveBalanceService;

    // ==================== AUTHENTICATION & PROFILE ====================

    @GetMapping("/profile")
    @Operation(summary = "Get employee profile", description = "Retrieve the profile of the currently authenticated employee")
    public ResponseEntity<ApiResponse> getProfile() {
        UserDTO profile = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(profile)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update employee profile", description = "Update basic profile information (phone, address, emergency contact)")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserDTO userDTO) {
        userService.updateProfile(userDTO);
        UserDTO updated = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Profile updated successfully")
                .data(updated)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/manager")
    @Operation(summary = "Get reporting manager", description = "View reporting manager details")
    public ResponseEntity<ApiResponse> getReportingManager() {
        UserDTO manager = userService.getReportingManager();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Reporting manager retrieved")
                .data(manager)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== LEAVE MANAGEMENT ====================

    @GetMapping("/leaves/balance")
    @Operation(summary = "Get leave balance", description = "View available leave balance by type (Casual, Sick, Paid)")
    public ResponseEntity<ApiResponse> getLeaveBalance() {
        Long userId = userService.getCurrentUser().getId();
        List<LeaveBalanceDTO> balances = leaveBalanceService.getEmployeeBalance(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave balance retrieved successfully")
                .data(balances)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/balance/{leaveTypeId}")
    @Operation(summary = "Get specific leave type balance", description = "View balance for a specific leave type")
    public ResponseEntity<ApiResponse> getLeaveTypeBalance(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave type balance retrieved")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/leaves/apply")
    @Operation(summary = "Apply for leave", description = "Submit leave application with start date, end date, leave type, and reason")
    public ResponseEntity<ApiResponse> applyLeave(@RequestBody LeaveApplicationDTO leaveApplicationDTO) {
        Long userId = userService.getCurrentUser().getId();
        com.revworkforce.entity.LeaveApplication application = leaveApplicationService.applyForLeave(userId, leaveApplicationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Leave application submitted successfully")
                .data(application)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/applications")
    @Operation(summary = "Get my leave applications", description = "View all leave applications with status (Pending/Approved/Rejected)")
    public ResponseEntity<ApiResponse> getMyLeaveApplications() {
        Long userId = userService.getCurrentUser().getId();
        List<LeaveApplicationDTO> applications = leaveApplicationService.getUserLeaveApplications(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave applications retrieved successfully")
                .data(applications)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/applications/{applicationId}")
    @Operation(summary = "Get leave application details", description = "Retrieve detailed information about a leave application")
    public ResponseEntity<ApiResponse> getLeaveApplicationDetails(@PathVariable Long applicationId) {
        LeaveApplicationDTO application = leaveApplicationService.getLeaveApplicationById(applicationId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave application details retrieved")
                .data(application)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @DeleteMapping("/leaves/applications/{applicationId}/cancel")
    @Operation(summary = "Cancel leave application", description = "Cancel a pending leave application")
    public ResponseEntity<ApiResponse> cancelLeaveApplication(@PathVariable Long applicationId) {
        leaveApplicationService.cancelLeave(applicationId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave application cancelled successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/holidays")
    @Operation(summary = "Get company holidays", description = "View company holiday calendar")
    public ResponseEntity<ApiResponse> getHolidays() {
        List<com.revworkforce.entity.Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Holidays retrieved successfully")
                .data(holidays)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== PERFORMANCE MANAGEMENT ====================

    @PostMapping("/performance-reviews")
    @Operation(summary = "Create performance review", description = "Submit self-appraisal with deliverables, accomplishments, improvements, and rating")
    public ResponseEntity<ApiResponse> createPerformanceReview(@RequestBody PerformanceReviewDTO reviewDTO) {
        Long userId = userService.getCurrentUser().getId();
        com.revworkforce.entity.PerformanceReview review = performanceReviewService.createPerformanceReview(userId, reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Performance review submitted successfully")
                .data(review)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/performance-reviews")
    @Operation(summary = "Get my performance reviews", description = "View submitted performance reviews with status")
    public ResponseEntity<ApiResponse> getMyPerformanceReviews() {
        Long userId = userService.getCurrentUser().getId();
        List<com.revworkforce.entity.PerformanceReview> reviews = performanceReviewService.getMyPerformanceReviews(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Performance reviews retrieved successfully")
                .data(reviews)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/performance-reviews/{reviewId}")
    @Operation(summary = "Get performance review details", description = "Retrieve detailed information about a performance review")
    public ResponseEntity<ApiResponse> getPerformanceReviewDetails(@PathVariable Long reviewId) {
        com.revworkforce.entity.PerformanceReview review = performanceReviewService.getPerformanceReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Performance review details retrieved")
                .data(review)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/performance-reviews/{reviewId}/feedback")
    @Operation(summary = "Get manager feedback", description = "View manager's feedback on performance review")
    public ResponseEntity<ApiResponse> getManagerFeedback(@PathVariable Long reviewId) {
        com.revworkforce.entity.ManagerFeedback feedback = performanceReviewService.getManagerFeedback(reviewId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Manager feedback retrieved")
                .data(feedback)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== EMPLOYEE GOALS ====================

    @PostMapping("/goals")
    @Operation(summary = "Set goal", description = "Create a new goal with description, deadline, and priority (High/Medium/Low)")
    public ResponseEntity<ApiResponse> setGoal(@RequestBody EmployeeGoalDTO goalDTO) {
        Long userId = userService.getCurrentUser().getId();
        com.revworkforce.entity.EmployeeGoal goal = employeeGoalService.setGoal(userId, goalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Goal created successfully")
                .data(goal)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/goals")
    @Operation(summary = "Get my goals", description = "View all goals with status")
    public ResponseEntity<ApiResponse> getMyGoals() {
        Long userId = userService.getCurrentUser().getId();
        List<com.revworkforce.entity.EmployeeGoal> goals = employeeGoalService.getMyGoals(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Goals retrieved successfully")
                .data(goals)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/goals/{goalId}")
    @Operation(summary = "Get goal details", description = "Retrieve detailed information about a goal")
    public ResponseEntity<ApiResponse> getGoalDetails(@PathVariable Long goalId) {
        com.revworkforce.entity.EmployeeGoal goal = employeeGoalService.getGoalById(goalId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Goal details retrieved")
                .data(goal)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/goals/{goalId}/progress")
    @Operation(summary = "Update goal progress", description = "Update goal progress percentage and status")
    public ResponseEntity<ApiResponse> updateGoalProgress(@PathVariable Long goalId, @RequestParam Integer progressPercentage, @RequestParam String status) {
        employeeGoalService.updateGoalProgress(goalId, progressPercentage, status);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Goal progress updated successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== ANNOUNCEMENTS ====================

    @GetMapping("/announcements")
    @Operation(summary = "Get announcements", description = "View company announcements")
    public ResponseEntity<ApiResponse> getAnnouncements() {
        List<com.revworkforce.entity.Announcement> announcements = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Announcements retrieved successfully")
                .data(announcements)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/announcements/{announcementId}")
    @Operation(summary = "Get announcement details", description = "Retrieve detailed information about an announcement")
    public ResponseEntity<ApiResponse> getAnnouncementDetails(@PathVariable Long announcementId) {
        com.revworkforce.entity.Announcement announcement = announcementService.getAnnouncementById(announcementId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Announcement details retrieved")
                .data(announcement)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== EMPLOYEE DIRECTORY ====================

    @GetMapping("/directory")
    @Operation(summary = "Get employee directory", description = "View all employees with search functionality")
    public ResponseEntity<ApiResponse> getEmployeeDirectory() {
        List<UserDTO> employees = userService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Employee directory retrieved successfully")
                .data(employees)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/directory/search")
    @Operation(summary = "Search in directory", description = "Search employees by name, ID, or department")
    public ResponseEntity<ApiResponse> searchDirectory(@RequestParam String search) {
        List<UserDTO> results = userService.searchEmployees(search);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Search results retrieved")
                .data(results)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    @Operation(summary = "Get notifications", description = "View all in-app notifications")
    public ResponseEntity<ApiResponse> getNotifications() {
        Long userId = userService.getCurrentUser().getId();
        List<com.revworkforce.entity.Notification> notifications = notificationService.getMyNotifications(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse> getUnreadNotificationCount() {
        int count = notificationService.getUnreadNotificationCount(userService.getCurrentUser().getId());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Unread count retrieved")
                .data(count)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/notifications/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a notification as read")
    public ResponseEntity<ApiResponse> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Notification marked as read")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/notifications/all/read")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ResponseEntity<ApiResponse> markAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead(userService.getCurrentUser().getId());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All notifications marked as read")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== REPORTS & QR CODE ====================

    @GetMapping("/report/download")
    @Operation(summary = "Download employee report", description = "Download personal employee report as PDF")
    public ResponseEntity<byte[]> downloadMyReport() {
        byte[] pdfContent = reportService.generateEmployeeReportBytes();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/qrcode")
    @Operation(summary = "Get QR code", description = "Get personal QR code in Base64 format")
    public ResponseEntity<ApiResponse> getMyQRCode() {
        String qrCode = qrCodeService.generateQRCodeForCurrentUser();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("QR code generated successfully")
                .data(qrCode)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/qrcode/image")
    @Operation(summary = "Get QR code image", description = "Get personal QR code as PNG image")
    public ResponseEntity<byte[]> getMyQRCodeImage() {
        return qrCodeService.generateQRCodeImageForCurrentUser();
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get employee dashboard", description = "Retrieve dashboard with key information (leave balance, pending leaves, etc)")
    public ResponseEntity<ApiResponse> getDashboard() {
        Long userId = userService.getCurrentUser().getId();
        Map<String, Object> data = new HashMap<>();
        data.put("leaveBalance", leaveBalanceService.getEmployeeBalance(userId));
        data.put("pendingLeaves", leaveApplicationService.getUserLeaveApplications(userId)
                .stream().filter(l -> l.getStatus().equals("PENDING")).count());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Dashboard retrieved successfully")
                .data(data)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
