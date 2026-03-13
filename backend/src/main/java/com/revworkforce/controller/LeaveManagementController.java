package com.revworkforce.controller;

import com.revworkforce.dto.LeaveApplicationDTO;
import com.revworkforce.entity.Holiday;
import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.LeaveBalance;
import com.revworkforce.entity.LeaveType;
import com.revworkforce.service.*;
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
@RequestMapping("/api/leaves")
@AllArgsConstructor
@Tag(name = "Leave Management", description = "Leave and holiday management endpoints")
public class LeaveManagementController {

    private final LeaveTypeService leaveTypeService;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveApplicationService leaveApplicationService;
    private final HolidayService holidayService;
    private final UserService userService;

    // ==================== LEAVE TYPE MANAGEMENT ====================

    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create leave type", description = "Create a new leave type")
    public ResponseEntity<ApiResponse> createLeaveType(@RequestBody LeaveType leaveType) {
        LeaveType created = leaveTypeService.createLeaveType(leaveType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Leave type created").data(created)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/types/{leaveTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update leave type", description = "Update leave type details")
    public ResponseEntity<ApiResponse> updateLeaveType(@PathVariable Long leaveTypeId, @RequestBody LeaveType leaveType) {
        LeaveType updated = leaveTypeService.updateLeaveType(leaveTypeId, leaveType);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave type updated").data(updated)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/types")
    @Operation(summary = "Get all leave types", description = "Retrieve list of all leave types")
    public ResponseEntity<ApiResponse> getAllLeaveTypes() {
        List<LeaveType> types = leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave types retrieved").data(types)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/types/active")
    @Operation(summary = "Get active leave types", description = "Retrieve list of active leave types")
    public ResponseEntity<ApiResponse> getActiveLeaveTypes() {
        List<LeaveType> types = leaveTypeService.getActiveLeaveTypes();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Active leave types retrieved").data(types)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== LEAVE BALANCE MANAGEMENT ====================

    @PostMapping("/balance/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign leave quota", description = "Assign leave quota to an employee")
    public ResponseEntity<ApiResponse> assignLeaveQuota(@RequestParam Long userId, @RequestParam Long leaveTypeId, @RequestParam Integer quota) {
        LeaveBalance balance = leaveBalanceService.assignLeaveQuota(userId, leaveTypeId, quota);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave quota assigned").data(balance)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/balance/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Adjust leave balance", description = "Manually adjust leave balance with reason")
    public ResponseEntity<ApiResponse> adjustLeaveBalance(@RequestParam Long userId, @RequestParam Long leaveTypeId, @RequestParam Integer adjustmentDays, @RequestParam String reason) {
        LeaveBalance balance = leaveBalanceService.adjustLeaveBalance(userId, leaveTypeId, adjustmentDays, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave balance adjusted").data(balance)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/balance/{userId}")
    @Operation(summary = "Get user leave balance", description = "Retrieve leave balance for a specific user")
    public ResponseEntity<ApiResponse> getUserLeaveBalance(@PathVariable Long userId) {
        List<LeaveBalance> balances = leaveBalanceService.getUserLeaveBalances(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave balance retrieved").data(balances)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== LEAVE APPLICATION MANAGEMENT ====================

    @PostMapping("/apply")
    @Operation(summary = "Apply for leave", description = "Submit a leave application")
    public ResponseEntity<ApiResponse> applyForLeave(@RequestBody LeaveApplicationDTO leaveDTO) {
        Long userId = userService.getCurrentUser().getId();
        LeaveApplication application = leaveApplicationService.applyForLeave(userId, leaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Leave application submitted").data(application)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{leaveApplicationId}/approve")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Approve leave", description = "Approve a pending leave application")
    public ResponseEntity<ApiResponse> approveLeave(@PathVariable Long leaveApplicationId, @RequestParam(required = false, defaultValue = "") String comment) {
        LeaveApplication approved = leaveApplicationService.approveLeave(leaveApplicationId, comment);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave approved successfully").data(approved)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{leaveApplicationId}/reject")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Reject leave", description = "Reject a pending leave application with mandatory comment")
    public ResponseEntity<ApiResponse> rejectLeave(@PathVariable Long leaveApplicationId, @RequestParam String comment) {
        LeaveApplication rejected = leaveApplicationService.rejectLeave(leaveApplicationId, comment);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave rejected successfully").data(rejected)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/{leaveApplicationId}/cancel")
    @Operation(summary = "Cancel leave", description = "Cancel a pending leave application")
    public ResponseEntity<ApiResponse> cancelLeave(@PathVariable Long leaveApplicationId) {
        leaveApplicationService.cancelLeave(leaveApplicationId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Leave application cancelled")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/applications/{userId}")
    @Operation(summary = "Get user leave applications", description = "Retrieve leave applications for a specific user")
    public ResponseEntity<ApiResponse> getUserLeaveApplications(@PathVariable Long userId) {
        List<LeaveApplicationDTO> applications = leaveApplicationService.getUserLeaveApplications(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("User applications retrieved").data(applications)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/applications/pending/team")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get team pending leaves", description = "Retrieve pending leave applications from team members")
    public ResponseEntity<ApiResponse> getTeamPendingLeaves() {
        List<LeaveApplicationDTO> applications = leaveApplicationService.getTeamPendingLeaves();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Team pending applications retrieved").data(applications)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== HOLIDAY MANAGEMENT ====================

    @PostMapping("/holidays")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create holiday", description = "Add a company holiday")
    public ResponseEntity<ApiResponse> createHoliday(@RequestBody Holiday holiday) {
        Holiday created = holidayService.createHoliday(holiday);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Holiday created").data(created)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/holidays/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update holiday", description = "Update holiday details")
    public ResponseEntity<ApiResponse> updateHoliday(@PathVariable Long holidayId, @RequestBody Holiday holiday) {
        Holiday updated = holidayService.updateHoliday(holidayId, holiday);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Holiday updated").data(updated)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/holidays/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete holiday", description = "Remove a company holiday")
    public ResponseEntity<ApiResponse> deleteHoliday(@PathVariable Long holidayId) {
        holidayService.deleteHoliday(holidayId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Holiday deleted")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/holidays")
    @Operation(summary = "Get all holidays", description = "Retrieve list of company holidays")
    public ResponseEntity<ApiResponse> getAllHolidays() {
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Holidays retrieved").data(holidays)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/bulk-assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> assignBulk(@RequestParam Long leaveTypeId, @RequestParam Integer quota) {
        leaveBalanceService.assignBulkLeave(leaveTypeId, quota);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Bulk leave assigned successfully to all employees")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}