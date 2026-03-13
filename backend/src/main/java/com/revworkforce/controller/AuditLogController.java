package com.revworkforce.controller;

import com.revworkforce.entity.AuditLog;
import com.revworkforce.service.AuditLogService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@AllArgsConstructor
@Tag(name = "Audit Logs", description = "System activity audit log endpoints")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAllAuditLogs(Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<AuditLog>>builder()
                .success(true)
                .message("Audit logs retrieved successfully")
                .data(logs)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user audit logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getUserAuditLogs(@PathVariable Long userId, Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getUserAuditLogs(userId, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<AuditLog>>builder()
                .success(true)
                .message("User audit logs retrieved successfully")
                .data(logs)
                .status("success")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}