package com.revworkforce.controller;

import com.revworkforce.entity.Announcement;
import com.revworkforce.service.AnnouncementService;
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
@RequestMapping("/api/announcements")
@AllArgsConstructor
@Tag(name = "Announcements", description = "Company announcement management endpoints")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create announcement")
    public ResponseEntity<ApiResponse> createAnnouncement(@RequestBody Announcement announcement) {
        Announcement created = announcementService.createAnnouncement(announcement);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("Announcement created successfully")
                        .data(created)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @PutMapping("/{announcementId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update announcement")
    public ResponseEntity<ApiResponse> updateAnnouncement(
            @PathVariable Long announcementId,
            @RequestBody Announcement announcement) {
        Announcement updated = announcementService.updateAnnouncement(announcementId, announcement);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Announcement updated successfully")
                        .data(updated)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete announcement")
    public ResponseEntity<ApiResponse> deleteAnnouncement(@PathVariable Long announcementId) {
        announcementService.deleteAnnouncement(announcementId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Announcement deleted successfully")
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping("/{announcementId}")
    @Operation(summary = "Get announcement")
    public ResponseEntity<ApiResponse> getAnnouncementById(@PathVariable Long announcementId) {
        Announcement announcement = announcementService.getAnnouncementById(announcementId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Announcement retrieved successfully")
                        .data(announcement)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get announcements")
    public ResponseEntity<ApiResponse> getAnnouncements(Pageable pageable) {
        Page<Announcement> announcements = announcementService.getActiveAnnouncements(pageable);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Announcements retrieved successfully")
                        .data(announcements)
                        .status("success")
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
}