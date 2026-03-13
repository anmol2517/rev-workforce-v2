package com.revworkforce.service;

import com.revworkforce.dto.PerformanceReviewDTO;
import com.revworkforce.entity.ManagerFeedback;
import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.entity.User;
import com.revworkforce.repository.ManagerFeedbackRepository;
import com.revworkforce.repository.PerformanceReviewRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final ManagerFeedbackRepository managerFeedbackRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public PerformanceReview createPerformanceReview(Long employeeId, PerformanceReviewDTO reviewDTO) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setManager(employee.getManager());
        review.setKeyDeliverables(reviewDTO.getKeyDeliverables());
        review.setAccomplishments(reviewDTO.getAccomplishments());
        review.setAreasOfImprovement(reviewDTO.getAreasOfImprovement());
        review.setEmployeeSelfRating(reviewDTO.getEmployeeSelfRating());
        review.setStatus(PerformanceReview.ReviewStatus.DRAFT);

        PerformanceReview savedReview = performanceReviewRepository.save(review);
        auditLogService.logAction("CREATE", "PERFORMANCE_REVIEW", savedReview.getId(), 
                "Performance review created by employee " + employee.getEmployeeId());
        return savedReview;
    }

    public PerformanceReview submitPerformanceReview(Long reviewId) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (!review.getStatus().equals(PerformanceReview.ReviewStatus.DRAFT)) {
            throw new RuntimeException("Only draft reviews can be submitted");
        }

        review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
        review.setSubmittedDate(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        PerformanceReview updatedReview = performanceReviewRepository.save(review);

        if (review.getEmployee().getManager() != null) {
            notificationService.createNotification(
                    review.getEmployee().getManager(),
                    "Performance Review Submitted",
                    review.getEmployee().getFirstName() + " " + review.getEmployee().getLastName() + " has submitted their performance review",
                    "FEEDBACK_SUBMITTED",
                    "PERFORMANCE_REVIEW",
                    updatedReview.getId()
            );
        }

        auditLogService.logAction("SUBMIT", "PERFORMANCE_REVIEW", reviewId, 
                "Performance review submitted by employee " + review.getEmployee().getEmployeeId());
        return updatedReview;
    }

    public ManagerFeedback submitManagerFeedback(Long reviewId, String feedback, Integer rating) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        if (!review.getStatus().equals(PerformanceReview.ReviewStatus.SUBMITTED)) {
            throw new RuntimeException("Review must be submitted before feedback can be added");
        }

        ManagerFeedback managerFeedback = managerFeedbackRepository.findByPerformanceReview(review)
                .orElse(new ManagerFeedback());

        managerFeedback.setPerformanceReview(review);
        managerFeedback.setManager(review.getEmployee().getManager());
        managerFeedback.setFeedback(feedback);
        managerFeedback.setManagerRating(rating);
        managerFeedback.setSubmittedDate(LocalDateTime.now());
        managerFeedback.setUpdatedAt(LocalDateTime.now());

        ManagerFeedback savedFeedback = managerFeedbackRepository.save(managerFeedback);

        review.setStatus(PerformanceReview.ReviewStatus.COMPLETED);
        review.setUpdatedAt(LocalDateTime.now());
        performanceReviewRepository.save(review);

        notificationService.createNotification(
                review.getEmployee(),
                "Feedback Received",
                "Your manager has provided feedback on your performance review",
                "FEEDBACK_SUBMITTED",
                "PERFORMANCE_REVIEW",
                reviewId
        );

        auditLogService.logAction("SUBMIT_FEEDBACK", "MANAGER_FEEDBACK", savedFeedback.getId(), 
                "Manager feedback submitted on performance review");
        return savedFeedback;
    }

    public List<PerformanceReviewDTO> getEmployeeReviews(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return performanceReviewRepository.findByEmployee(employee)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PerformanceReviewDTO> getManagerReviews() {
        User manager = userRepository.findAll()
                .stream()
                .filter(u -> u.getId().equals(getCurrentUserId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return performanceReviewRepository.findSubmittedReviewsByManager(manager)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerformanceReviewDTO getReviewById(Long reviewId) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));
        return convertToDTO(review);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private PerformanceReviewDTO convertToDTO(PerformanceReview review) {
        PerformanceReviewDTO dto = new PerformanceReviewDTO();
        dto.setId(review.getId());
        dto.setEmployeeId(review.getEmployee().getId());
        dto.setEmployeeName(review.getEmployee().getFirstName() + " " + review.getEmployee().getLastName());
        if (review.getManager() != null) {
            dto.setManagerId(review.getManager().getId());
            dto.setManagerName(review.getManager().getFirstName() + " " + review.getManager().getLastName());
        }
        dto.setKeyDeliverables(review.getKeyDeliverables());
        dto.setAccomplishments(review.getAccomplishments());
        dto.setAreasOfImprovement(review.getAreasOfImprovement());
        dto.setEmployeeSelfRating(review.getEmployeeSelfRating());
        dto.setStatus(review.getStatus().toString());
        dto.setSubmittedDate(review.getSubmittedDate());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }

    public List<PerformanceReview> getMyPerformanceReviews(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return performanceReviewRepository.findByEmployee(employee);
    }

    public PerformanceReview getPerformanceReviewById(Long reviewId) {
        return performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public ManagerFeedback getManagerFeedback(Long reviewId) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return managerFeedbackRepository.findByPerformanceReview(review)
                .orElseThrow(() -> new RuntimeException("Feedback not found yet"));
    }

    public List<PerformanceReview> getTeamPerformanceReviews() {
        User manager = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return performanceReviewRepository.findSubmittedReviewsByManager(manager);
    }

    public ManagerFeedback provideManagerFeedback(Long reviewId, com.revworkforce.dto.ManagerFeedbackDTO feedbackDTO) {
        return submitManagerFeedback(reviewId, feedbackDTO.getFeedback(), feedbackDTO.getManagerRating());
    }
}
