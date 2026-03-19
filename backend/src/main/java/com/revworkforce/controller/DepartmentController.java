package com.revworkforce.controller;

import com.revworkforce.entity.Department;
import com.revworkforce.service.DepartmentService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/departments")
@AllArgsConstructor
@Tag(name = "Departments", description = "Department management endpoints")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create department")
    public ResponseEntity<ApiResponse> createDepartment(@RequestBody Department department) {
        Department created = departmentService.createDepartment(department);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("Department created successfully")
                        .data(created)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @PutMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department")
    public ResponseEntity<ApiResponse> updateDepartment(@PathVariable Long departmentId, @RequestBody Department department) {
        Department updated = departmentService.updateDepartment(departmentId, department);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department updated successfully")
                        .data(updated)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @DeleteMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department")
    public ResponseEntity<ApiResponse> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department deleted successfully")
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping("/{departmentId}")
    @Operation(summary = "Get department")
    public ResponseEntity<ApiResponse> getDepartmentById(@PathVariable Long departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department retrieved successfully")
                        .data(department)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<ApiResponse> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Departments retrieved successfully")
                        .data(departments)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create multiple departments")
    public ResponseEntity<ApiResponse> createDepartmentsBulk(@RequestBody List<Department> departments) {
        List<Department> created = departmentService.createAllDepartments(departments);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("All departments created successfully")
                        .data(created)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
}