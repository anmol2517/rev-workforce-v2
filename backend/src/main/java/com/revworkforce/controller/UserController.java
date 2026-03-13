package com.revworkforce.controller;

import com.revworkforce.dto.UserDTO;
import com.revworkforce.entity.User;
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
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    // ==================== PROFILE MANAGEMENT ====================

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    public ResponseEntity<ApiResponse> getProfile() {
        UserDTO profile = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Profile retrieved successfully").data(profile)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update basic profile information of the current user")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserDTO userDTO) {
        userService.updateProfile(userDTO);
        UserDTO updated = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Profile updated successfully").data(updated)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== ADMIN & EMPLOYEE SEARCH ====================

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details", description = "Retrieve detailed information about a specific user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("User details retrieved").data(user)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new employee (Admin Only)", description = "Creates a new user with specific role and assigns management hierarchy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody UserDTO userDTO) {
        User savedUser = userService.addEmployee(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message("Employee added successfully").data(savedUser)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name, ID, department, or designation")
    public ResponseEntity<ApiResponse> searchEmployees(@RequestParam String search) {
        List<UserDTO> results = userService.searchEmployees(search);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Search results retrieved").data(results)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/team/members")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get team members", description = "Retrieve list of direct reportees for a manager")
    public ResponseEntity<ApiResponse> getTeamMembers() {
        List<UserDTO> members = userService.getTeamMembers();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Team members retrieved successfully").data(members)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    // ==================== ADMIN EXCLUSIVE ACTIONS ====================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all employees", description = "List all active employees")
    public ResponseEntity<ApiResponse> getAllEmployees() {
        List<UserDTO> employees = userService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("All employees retrieved").data(employees)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update employee", description = "Update employee details (Admin only)")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        userService.updateEmployee(userId, userDTO);
        UserDTO updated = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Employee details updated").data(updated)
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate employee", description = "Deactivate an employee account")
    public ResponseEntity<ApiResponse> deactivateEmployee(@PathVariable Long userId) {
        userService.deactivateEmployee(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Employee account deactivated")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{userId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivate employee", description = "Reactivate a deactivated employee account")
    public ResponseEntity<ApiResponse> reactivateEmployee(@PathVariable Long userId) {
        userService.reactivateEmployee(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Employee account reactivated")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{employeeId}/manager/{managerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign manager", description = "Assign or change reporting manager for an employee")
    public ResponseEntity<ApiResponse> assignManager(@PathVariable Long employeeId, @PathVariable Long managerId) {
        userService.assignManager(employeeId, managerId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Manager assigned successfully")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/add/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add multiple employees")
    public ResponseEntity<ApiResponse> addEmployeesBulk(@RequestBody List<UserDTO> userDTOs) {
        userDTOs.forEach(userService::addEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true).message(userDTOs.size() + " Users added successfully")
                .status("success").timestamp(System.currentTimeMillis()).build());
    }
}
