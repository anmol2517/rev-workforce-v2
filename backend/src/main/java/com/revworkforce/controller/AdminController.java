package com.revworkforce.controller;

import com.revworkforce.dto.UserDTO;
import com.revworkforce.entity.*;
import com.revworkforce.repository.EmployeeGoalRepository;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.service.*;
import com.revworkforce.util.ApiResponse;
import com.revworkforce.util.ExcelGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Admin-only endpoints for system management")
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final DesignationService designationService;
    private final LeaveTypeService leaveTypeService;
    private final LeaveBalanceService leaveBalanceService;
    private final HolidayService holidayService;
    private final AnnouncementService announcementService;
    private final AuditLogService auditLogService;
    private final ReportService reportService;
    private final LeaveApplicationService leaveApplicationService;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeGoalRepository employeeGoalRepository;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get Dashboard Metrics", description = "Retrieves total counts and chart data for leaves and goals")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEmployees", userService.getAllEmployees().size());
        stats.put("totalDepartments", departmentService.getAllDepartments().size());
        stats.put("activeAnnouncements", announcementService.getActiveAnnouncements().size());

        Map<String, Long> leaveMetrics = new HashMap<>();
        leaveMetrics.put("pending", leaveApplicationRepository.countByStatus(com.revworkforce.entity.LeaveApplication.LeaveStatus.PENDING));
        leaveMetrics.put("approved", leaveApplicationRepository.countByStatus(com.revworkforce.entity.LeaveApplication.LeaveStatus.APPROVED));
        leaveMetrics.put("rejected", leaveApplicationRepository.countByStatus(com.revworkforce.entity.LeaveApplication.LeaveStatus.REJECTED));
        stats.put("leaveStats", leaveMetrics);

        Map<String, Long> goalMetrics = new HashMap<>();
        goalMetrics.put("completed", employeeGoalRepository.countByStatus(com.revworkforce.entity.EmployeeGoal.GoalStatus.COMPLETED));
        goalMetrics.put("inProgress", employeeGoalRepository.countByStatus(com.revworkforce.entity.EmployeeGoal.GoalStatus.IN_PROGRESS));
        goalMetrics.put("notStarted", employeeGoalRepository.countByStatus(com.revworkforce.entity.EmployeeGoal.GoalStatus.NOT_STARTED));
        stats.put("goalStats", goalMetrics);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Dashboard stats retrieved successfully")
                .data(stats)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/reports/employees/excel")
    @Operation(summary = "Export Employees to Excel")
    public ResponseEntity<byte[]> exportEmployeesExcel() throws IOException {
        List<UserDTO> employees = userService.getAllEmployees();
        byte[] excelContent = ExcelGenerator.employeesToExcel(employees);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("employees_report.xlsx").build());

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping("/reports/employee/{id}/pdf")
    @Operation(summary = "Export Employee PDF Report")
    public ResponseEntity<byte[]> downloadEmployeeReport(@PathVariable Long id) throws Exception {
        byte[] pdfContent = reportService.generateEmployeeReport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("employee_report_" + id + ".pdf").build());

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @PostMapping("/leaves/types/bulk-create")
    @Operation(summary = "Bulk create leave types", description = "Add multiple leave types at once")
    public ResponseEntity<ApiResponse> bulkCreateLeaveTypes(@RequestBody List<LeaveType> leaveTypes) {
        List<LeaveType> createdTypes = leaveTypeService.saveAll(leaveTypes);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Leave types created successfully in bulk")
                .data(createdTypes)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/types")
    @Operation(summary = "Get all leave types")
    public ResponseEntity<ApiResponse> getAllLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave types retrieved successfully")
                .data(leaveTypes)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/leaves/types/{leaveTypeId}")
    @Operation(summary = "Update leave type")
    public ResponseEntity<ApiResponse> updateLeaveType(@PathVariable Long leaveTypeId, @RequestBody LeaveType leaveType) {
        LeaveType updated = leaveTypeService.updateLeaveType(leaveTypeId, leaveType);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave type updated successfully")
                .data(updated)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @DeleteMapping("/leaves/types/{leaveTypeId}")
    @Operation(summary = "Delete leave type")
    public ResponseEntity<ApiResponse> deleteLeaveType(@PathVariable Long leaveTypeId) {
        leaveTypeService.deleteLeaveType(leaveTypeId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave type deleted successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/goals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllGoals() {
        List<EmployeeGoal> goals = employeeGoalRepository.findAll();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("All goals retrieved").data(goals)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/announcements")
    @Operation(summary = "Create company announcement")
    public ResponseEntity<ApiResponse> createAnnouncement(@RequestBody com.revworkforce.entity.Announcement announcement) {
        com.revworkforce.entity.Announcement created = announcementService.createAnnouncement(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Announcement posted successfully")
                .data(created)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/leaves/applications/{id}/approve")
    @Operation(summary = "Approve a leave application")
    public ResponseEntity<ApiResponse> approveLeave(@PathVariable Long id, @RequestParam(required = false) String comment) {
        LeaveApplication approved = leaveApplicationService.approveLeave(id, comment != null ? comment : "Approved by Admin");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave application approved successfully")
                .data(approved)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/leaves/applications/{id}/reject")
    @Operation(summary = "Reject a leave application")
    public ResponseEntity<ApiResponse> rejectLeave(@PathVariable Long id, @RequestParam String reason) {
        LeaveApplication rejected = leaveApplicationService.rejectLeave(id, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Leave application rejected")
                .data(rejected)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody UserDTO userDTO) {
        try {
            User created = userService.addEmployee(userDTO);
            auditLogService.logAction("CREATE", "EMPLOYEE", created.getId(), "Admin added employee: " + created.getEmployeeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                    .success(true)
                    .message("Employee added successfully")
                    .data(created)
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @PostMapping("/employees/bulk")
    public ResponseEntity<ApiResponse> addEmployeesBulk(@RequestBody List<UserDTO> userDTOs) {
        try {
            List<User> created = userService.addEmployeesBulk(userDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                    .success(true)
                    .message(created.size() + " Employees added successfully")
                    .data(created)
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse> getAllEmployees() {
        try {
            List<UserDTO> employees = userService.getAllEmployees();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Employees retrieved successfully")
                    .data(employees)
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User updated = userService.updateEmployee(id, userDTO);
            auditLogService.logAction("UPDATE", "EMPLOYEE", id, "Admin updated employee: " + updated.getEmployeeId());
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Employee updated")
                    .data(updated)
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        try {
            userService.deleteEmployee(id);
            auditLogService.logAction("DELETE", "EMPLOYEE", id, "Admin deleted employee record");
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Employee deleted")
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @GetMapping("/employees/search")
    public ResponseEntity<ApiResponse> searchEmployees(@RequestParam String query) {
        try {
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .data(userService.searchEmployees(query))
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status("error")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }

    @PutMapping("/employees/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable Long id) {
        userService.deactivateEmployee(id);
        auditLogService.logAction("DEACTIVATE", "EMPLOYEE", id, "Admin deactivated employee account");
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Employee deactivated").build());
    }

    @PutMapping("/employees/{id}/activate")
    public ResponseEntity<ApiResponse> activate(@PathVariable Long id) {
        userService.reactivateEmployee(id);
        auditLogService.logAction("ACTIVATE", "EMPLOYEE", id, "Admin activated employee account");
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Employee activated").build());
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View system activity logs", description = "View all audit logs with pagination")
    public ResponseEntity<ApiResponse> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("System activity logs retrieved successfully")
                .data(auditLogService.getAllAuditLogs(pageable))
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/leaves/utilization")
    @Operation(summary = "Get leave utilization stats for Admin")
    public ResponseEntity<ApiResponse> getLeaveUtilization() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .data(leaveApplicationService.getLeaveUtilizationStats())
                .build());
    }

    @GetMapping("/leaves/all")
    @Operation(summary = "View all employee leave information")
    public ResponseEntity<ApiResponse> getAllLeaves() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .data(leaveApplicationService.getAllEmployeeLeaves())
                .build());
    }

    @PutMapping("/employees/{employeeId}/assign-manager/{managerId}")
    @Operation(summary = "Assign a manager to an employee")
    public ResponseEntity<ApiResponse> assignManager(@PathVariable Long employeeId, @PathVariable Long managerId) {
        userService.assignManager(employeeId, managerId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Manager assigned successfully")
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
