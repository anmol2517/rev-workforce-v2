package com.revworkforce.controller;

import com.revworkforce.dto.EmployeeGoalDTO;
import com.revworkforce.dto.ManagerFeedbackDTO;
import com.revworkforce.dto.PerformanceReviewDTO;
import com.revworkforce.entity.EmployeeGoal;
import com.revworkforce.entity.ManagerFeedback;
import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.service.EmployeeGoalService;
import com.revworkforce.service.PerformanceReviewService;
import com.revworkforce.service.UserService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@AllArgsConstructor
@Tag(name = "Performance Management", description = "Performance review and goal management endpoints")
public class PerformanceController {

    private final PerformanceReviewService performanceReviewService;
    private final EmployeeGoalService employeeGoalService;
    private final UserService userService;

    // ==================== PERFORMANCE REVIEWS ====================

    @PostMapping("/reviews")
    @Operation(summary = "Create performance review", description = "Create a new performance review")
    public ResponseEntity<ApiResponse> createPerformanceReview(@RequestBody PerformanceReviewDTO reviewDTO) {
        Long employeeId = userService.getCurrentUser().getId();
        PerformanceReview review = performanceReviewService.createPerformanceReview(employeeId, reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Performance review created").data(review)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/reviews/{reviewId}/submit")
    @Operation(summary = "Submit performance review", description = "Submit a draft performance review")
    public ResponseEntity<ApiResponse> submitPerformanceReview(@PathVariable Long reviewId) {
        PerformanceReview review = performanceReviewService.submitPerformanceReview(reviewId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Performance review submitted successfully").data(review)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/reviews/employee/{employeeId}")
    @Operation(summary = "Get employee reviews", description = "Retrieve all performance reviews for an employee")
    public ResponseEntity<ApiResponse> getEmployeeReviews(@PathVariable Long employeeId) {
        List<PerformanceReviewDTO> reviews = performanceReviewService.getEmployeeReviews(employeeId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Employee reviews retrieved").data(reviews)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/reviews/manager/submitted")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get submitted reviews", description = "Retrieve submitted performance reviews for team members")
    public ResponseEntity<ApiResponse> getManagerReviews() {
        List<PerformanceReviewDTO> reviews = performanceReviewService.getManagerReviews();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Manager team reviews retrieved").data(reviews)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/reviews/{reviewId}")
    @Operation(summary = "Get performance review", description = "Retrieve detailed performance review")
    public ResponseEntity<ApiResponse> getReviewById(@PathVariable Long reviewId) {
        PerformanceReviewDTO review = performanceReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Review details retrieved").data(review)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== MANAGER FEEDBACK ====================

    @PostMapping("/feedback/{reviewId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Submit manager feedback", description = "Provide manager feedback on employee performance review")
    public ResponseEntity<ApiResponse> submitManagerFeedback(
            @PathVariable Long reviewId,
            @RequestParam String feedback,
            @RequestParam Integer rating) {
        ManagerFeedback managerFeedback = performanceReviewService.submitManagerFeedback(reviewId, feedback, rating);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Manager feedback submitted").data(managerFeedback)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== EMPLOYEE GOALS ====================

    @PostMapping("/goals")
    @Operation(summary = "Create goal", description = "Create a new employee goal")
    public ResponseEntity<ApiResponse> createGoal(@RequestBody EmployeeGoalDTO goalDTO) {
        // FIXED: Using dynamic ID from context
        Long employeeId = userService.getCurrentUser().getId();
        EmployeeGoal goal = employeeGoalService.createGoal(employeeId, goalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Goal created successfully").data(goal)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/goals/{goalId}")
    @Operation(summary = "Update goal", description = "Update goal details")
    public ResponseEntity<ApiResponse> updateGoal(@PathVariable Long goalId, @RequestBody EmployeeGoalDTO goalDTO) {
        EmployeeGoal goal = employeeGoalService.updateGoal(goalId, goalDTO);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Goal updated successfully").data(goal)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/goals/{goalId}/progress")
    @Operation(summary = "Update goal progress", description = "Update goal progress and comments")
    public ResponseEntity<ApiResponse> updateGoalProgress(
            @PathVariable Long goalId,
            @RequestParam Integer progressPercentage,
            @RequestParam String progressComment) {
        employeeGoalService.updateGoalProgress(goalId, progressPercentage, progressComment);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Goal progress updated")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/goals/{goalId}")
    @Operation(summary = "Cancel goal", description = "Cancel a goal")
    public ResponseEntity<ApiResponse> cancelGoal(@PathVariable Long goalId) {
        employeeGoalService.cancelGoal(goalId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Goal cancelled successfully")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/goals/{employeeId}")
    @Operation(summary = "Get employee goals", description = "Retrieve all goals for an employee")
    public ResponseEntity<ApiResponse> getEmployeeGoals(@PathVariable Long employeeId) {
        List<EmployeeGoalDTO> goals = employeeGoalService.getEmployeeGoals(employeeId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Employee goals retrieved").data(goals)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/goals/{employeeId}/status/{status}")
    @Operation(summary = "Get goals by status", description = "Retrieve goals filtered by status")
    public ResponseEntity<ApiResponse> getGoalsByStatus(
            @PathVariable Long employeeId,
            @PathVariable String status) {
        List<EmployeeGoalDTO> goals = employeeGoalService.getGoalsByStatus(employeeId, status);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Filtered goals retrieved").data(goals)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }
}