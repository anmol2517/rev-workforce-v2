package com.revworkforce.controller;

import com.revworkforce.dto.LeaveApplicationDTO;
import com.revworkforce.dto.ManagerFeedbackDTO;
import com.revworkforce.dto.NotificationDTO;
import com.revworkforce.dto.UserDTO;
import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.ManagerFeedback;
import com.revworkforce.service.*;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'ROLE_ADMIN')")
@Tag(name = "Manager Management", description = "Manager-only endpoints for team management")
public class ManagerController {

    private final UserService userService;
    private final LeaveApplicationService leaveApplicationService;
    private final PerformanceReviewService performanceReviewService;
    private final EmployeeGoalService employeeGoalService;
    private final AnnouncementService announcementService;
    private final LeaveBalanceService leaveBalanceService;
    private final NotificationService notificationService;

    // ==================== TEAM MANAGEMENT ====================

    @GetMapping("/team-members")
    @Operation(summary = "Get team members", description = "Retrieve list of direct reportees")
    public ResponseEntity<ApiResponse> getTeamMembers() {
        List<UserDTO> members = userService.getTeamMembers();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Team members retrieved successfully")
                .data(members)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/team-members/{employeeId}")
    @Operation(summary = "Get team member details", description = "Retrieve detailed information about a team member")
    public ResponseEntity<ApiResponse> getTeamMemberDetails(@PathVariable Long employeeId) {
        UserDTO member = userService.getUserById(employeeId);
        UserDTO currentManager = userService.getUserProfile();

        if (!currentManager.getRole().equals("ADMIN") &&
                (member.getManagerId() == null || !member.getManagerId().equals(currentManager.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.builder()
                    .success(false)
                    .message("Access Denied: Not your team member")
                    .status("error")
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Team member details retrieved")
                .data(member)
                .status("success")
                .build());
    }

    // ==================== LEAVE MANAGEMENT ====================

    @GetMapping("/leaves/applications")
    @Operation(summary = "Get team leave applications", description = "View leave applications from team members")
    public ResponseEntity<ApiResponse> getTeamLeaveApplications() {
        List<LeaveApplicationDTO> applications = leaveApplicationService.getTeamPendingLeaves();
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

    @PutMapping("/leaves/applications/{applicationId}/approve")
    @Operation(summary = "Approve leave request", description = "Approve a team member's leave request")
    public ResponseEntity<ApiResponse> approveLeave(@PathVariable Long applicationId, @RequestParam(required = false) String comments) {
        leaveApplicationService.approveLeave(applicationId, comments);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave approved successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/leaves/applications/{applicationId}/reject")
    @Operation(summary = "Reject leave request", description = "Reject a team member's leave request with mandatory comments")
    public ResponseEntity<ApiResponse> rejectLeave(@PathVariable Long applicationId, @RequestParam String comments) {
        leaveApplicationService.rejectLeave(applicationId, comments);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave rejected successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/team-calendar")
    @Operation(summary = "Get team leave calendar", description = "View team members' leaves in calendar format")
    public ResponseEntity<ApiResponse> getTeamLeaveCalendar() {
        List<UserDTO> team = userService.getTeamMembers();

        List<Map<String, Object>> calendar = team.stream()
                .map(member -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("employeeName", member.getFirstName() + " " + member.getLastName());
                    map.put("leaves", leaveApplicationService.getUserLeaveApplications(member.getId()));
                    return map;
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Team leave calendar retrieved")
                .data(calendar)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/team-balance")
    @Operation(summary = "Get team leave balance", description = "View leave balance of all team members")
    public ResponseEntity<ApiResponse> getTeamLeaveBalance() {
        List<UserDTO> team = userService.getTeamMembers();
        List<List<com.revworkforce.dto.LeaveBalanceDTO>> balances = team.stream()
                .map(member -> leaveBalanceService.getEmployeeBalance(member.getId()))
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Team leave balance retrieved")
                .data(balances)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== PERFORMANCE MANAGEMENT ====================

    @GetMapping("/performance-reviews")
    @Operation(summary = "Get team performance reviews", description = "View performance reviews submitted by direct reportees")
    public ResponseEntity<ApiResponse> getTeamPerformanceReviews() {
        List<com.revworkforce.entity.PerformanceReview> reviews = performanceReviewService.getTeamPerformanceReviews();
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

    @PostMapping("/performance-reviews/{reviewId}/feedback")
    @Operation(summary = "Provide performance feedback", description = "Provide detailed feedback on employee performance review with rating")
    public ResponseEntity<ApiResponse> providePerformanceFeedback(@PathVariable Long reviewId, @RequestBody ManagerFeedbackDTO feedbackDTO) {
        ManagerFeedback feedback = performanceReviewService.provideManagerFeedback(reviewId, feedbackDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Feedback provided successfully")
                .data(feedback)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/performance-reviews/{reviewId}/feedback")
    @Operation(summary = "Get performance feedback", description = "Retrieve manager feedback on employee's performance review")
    public ResponseEntity<ApiResponse> getPerformanceFeedback(@PathVariable Long reviewId) {
        ManagerFeedback feedback = performanceReviewService.getManagerFeedback(reviewId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Performance feedback retrieved")
                .data(feedback)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== EMPLOYEE GOALS ====================

    @GetMapping("/goals")
    @Operation(summary = "Get team goals", description = "View goals set by team members")
    public ResponseEntity<ApiResponse> getTeamGoals() {
        List<com.revworkforce.entity.EmployeeGoal> goals = employeeGoalService.getTeamGoals();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Team goals retrieved successfully")
                .data(goals)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/goals/{goalId}")
    @Operation(summary = "Get goal details", description = "Retrieve detailed information about an employee goal")
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

    @PutMapping("/goals/{goalId}/comments")
    @Operation(summary = "Add goal comments", description = "Provide comments on employee goal")
    public ResponseEntity<ApiResponse> addGoalComments(@PathVariable Long goalId, @RequestParam String comments) {
        employeeGoalService.addManagerComments(goalId, comments);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Comments added successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== ANNOUNCEMENTS ====================

    @PostMapping("/announcements")
    @Operation(summary = "Create team announcement", description = "Create an announcement for team members")
    public ResponseEntity<ApiResponse> createAnnouncement(@RequestBody com.revworkforce.entity.Announcement announcement) {
        com.revworkforce.entity.Announcement created = announcementService.createAnnouncement(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Announcement created successfully")
                .data(created)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    // ==================== EMPLOYEE FEATURES (Available to Managers as well) ====================

    @GetMapping("/profile")
    @Operation(summary = "Get manager profile", description = "Retrieve the profile of the current manager")
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
    @Operation(summary = "Update manager profile", description = "Update basic profile information")
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

    @PostMapping("/goals")
    @Operation(summary = "Assign goal to team member", description = "Manager assigns a new goal to a specific team member")
    public ResponseEntity<ApiResponse> assignGoal(@RequestBody com.revworkforce.dto.EmployeeGoalDTO goalDTO) {
        com.revworkforce.entity.EmployeeGoal createdGoal = employeeGoalService.createGoal(goalDTO.getEmployeeId(), goalDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Goal assigned successfully")
                .data(createdGoal)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get Manager Dashboard Stats", description = "Retrieves stats for team members, pending leaves, and goals")
    public ResponseEntity<ApiResponse> getManagerDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<UserDTO> team = userService.getTeamMembers();
        stats.put("teamSize", team.size());
        stats.put("pendingLeaves", leaveApplicationService.getTeamPendingLeaves().size());
        stats.put("teamGoals", employeeGoalService.getTeamGoals().size());

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Manager dashboard stats retrieved")
                .data(stats)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get manager notifications", description = "View all notifications for the logged-in manager")
    public ResponseEntity<ApiResponse> getManagerNotifications() {
        UserDTO currentManager = userService.getUserProfile();
        List<NotificationDTO> notifications = notificationService.getAllUserNotifications(currentManager.getId());

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

}