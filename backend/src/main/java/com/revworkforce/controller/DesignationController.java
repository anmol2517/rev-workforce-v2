package com.revworkforce.controller;

import com.revworkforce.entity.Designation;
import com.revworkforce.service.DesignationService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/designations")
@AllArgsConstructor
@Tag(name = "Designations", description = "Job title/designation management endpoints")
public class DesignationController {

    private final DesignationService designationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create designation")
    public ResponseEntity<ApiResponse> createDesignation(@RequestBody Designation designation) {
        Designation created = designationService.createDesignation(designation);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation created successfully")
                        .data(created)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @PutMapping("/{designationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update designation")
    public ResponseEntity<ApiResponse> updateDesignation(@PathVariable Long designationId, @RequestBody Designation designation) {
        Designation updated = designationService.updateDesignation(designationId, designation);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation updated successfully")
                        .data(updated)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @DeleteMapping("/{designationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete designation")
    public ResponseEntity<ApiResponse> deleteDesignation(@PathVariable Long designationId) {
        designationService.deleteDesignation(designationId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation deleted successfully")
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping("/{designationId}")
    @Operation(summary = "Get designation")
    public ResponseEntity<ApiResponse> getDesignationById(@PathVariable Long designationId) {
        Designation designation = designationService.getDesignationById(designationId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation retrieved successfully")
                        .data(designation)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get all designations")
    public ResponseEntity<ApiResponse> getAllDesignations() {
        List<Designation> designations = designationService.getAllDesignations();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designations retrieved successfully")
                        .data(designations)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create multiple designations")
    public ResponseEntity<ApiResponse> createDesignationsBulk(@RequestBody List<Designation> designations) {
        List<Designation> created = designationService.createAllDesignations(designations);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("All designations created successfully")
                        .data(created)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
}