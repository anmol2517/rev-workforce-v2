package com.revworkforce.service;

import com.revworkforce.entity.Announcement;
import com.revworkforce.entity.User;
import com.revworkforce.repository.AnnouncementRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public Announcement createAnnouncement(Announcement announcement) {
        User currentUser = getCurrentUser();

        announcement.setCreatedBy(currentUser);
        announcement.setPublishDate(LocalDateTime.now());

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        

        sendAnnouncementNotifications(currentUser, savedAnnouncement);
        
        auditLogService.logAction("CREATE", "ANNOUNCEMENT", savedAnnouncement.getId(), 
                "Announcement created: " + announcement.getTitle());
        return savedAnnouncement;
    }

    public Announcement updateAnnouncement(Long announcementId, Announcement announcementDetails) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setTitle(announcementDetails.getTitle());
        announcement.setContent(announcementDetails.getContent());
        announcement.setIsActive(announcementDetails.getIsActive());
        announcement.setUpdatedAt(LocalDateTime.now());

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        auditLogService.logAction("UPDATE", "ANNOUNCEMENT", announcementId, 
                "Announcement updated: " + announcement.getTitle());
        return updatedAnnouncement;
    }

    public void deleteAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcementRepository.delete(announcement);
        auditLogService.logAction("DELETE", "ANNOUNCEMENT", announcementId, 
                "Announcement deleted: " + announcement.getTitle());
    }

    public Announcement getAnnouncementById(Long announcementId) {
        return announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    public Page<Announcement> getActiveAnnouncements(Pageable pageable) {
        return announcementRepository.findByIsActiveTrue(pageable);
    }

    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findAll()
                .stream()
                .filter(Announcement::getIsActive)
                .toList();
    }

    private void sendAnnouncementNotifications(User createdBy, Announcement announcement) {
        String role = createdBy.getRole().name();

        if ("ADMIN".equals(role)) {
            List<User> allUsers = userRepository.findAll();
            allUsers.forEach(user -> {
                if (!user.getId().equals(createdBy.getId())) {
                    notificationService.createNotification(
                            user,
                            "New Announcement",
                            announcement.getTitle(),
                            "ANNOUNCEMENT",
                            "ANNOUNCEMENT",
                            announcement.getId()
                    );
                }
            });
        } else if ("MANAGER".equals(role)) {
            List<User> teamMembers = userRepository.findByManager(createdBy);
            teamMembers.forEach(user -> 
                notificationService.createNotification(
                        user,
                        "Team Announcement",
                        announcement.getTitle(),
                        "ANNOUNCEMENT",
                        "ANNOUNCEMENT",
                        announcement.getId()
                )
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
