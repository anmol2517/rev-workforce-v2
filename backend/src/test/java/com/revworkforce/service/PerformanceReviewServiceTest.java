package com.revworkforce.service;

import com.revworkforce.dto.PerformanceReviewDTO;
import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.entity.User;
import com.revworkforce.repository.ManagerFeedbackRepository;
import com.revworkforce.repository.PerformanceReviewRepository;
import com.revworkforce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformanceReviewServiceTest {

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private ManagerFeedbackRepository managerFeedbackRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PerformanceReviewService performanceReviewService;

    private User testEmployee;
    private PerformanceReview testReview;
    private PerformanceReviewDTO testReviewDTO;

    @BeforeEach
    public void setUp() {
        testEmployee = new User();
        testEmployee.setId(1L);
        testEmployee.setEmployeeId("EMP001");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");

        testReviewDTO = new PerformanceReviewDTO();
        testReviewDTO.setEmployeeSelfRating(4);
        testReviewDTO.setKeyDeliverables("Completed projects on time");
        testReviewDTO.setAccomplishments("Led team meetings");
        testReviewDTO.setAreasOfImprovement("Improve documentation");

        testReview = new PerformanceReview();
        testReview.setId(1L);
        testReview.setEmployee(testEmployee);
        testReview.setStatus(PerformanceReview.ReviewStatus.DRAFT);
        testReview.setEmployeeSelfRating(testReviewDTO.getEmployeeSelfRating());
        testReview.setKeyDeliverables(testReviewDTO.getKeyDeliverables());
        testReview.setAccomplishments(testReviewDTO.getAccomplishments());
        testReview.setAreasOfImprovement(testReviewDTO.getAreasOfImprovement());
    }

    @Test
    public void testCreatePerformanceReview_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(testReview);

        PerformanceReview result = performanceReviewService.createPerformanceReview(1L, testReviewDTO);

        assertNotNull(result);
        assertEquals(PerformanceReview.ReviewStatus.DRAFT, result.getStatus());
        verify(performanceReviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    public void testCreatePerformanceReview_EmployeeNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> performanceReviewService.createPerformanceReview(999L, testReviewDTO));
    }

    @Test
    public void testSubmitPerformanceReview_Success() {
        testReview.setStatus(PerformanceReview.ReviewStatus.DRAFT);
        when(performanceReviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(testReview);

        PerformanceReview result = performanceReviewService.submitPerformanceReview(1L);

        assertEquals(PerformanceReview.ReviewStatus.SUBMITTED, result.getStatus());
        verify(performanceReviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    public void testSubmitPerformanceReview_NotDraft() {
        testReview.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
        when(performanceReviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        assertThrows(RuntimeException.class, () -> performanceReviewService.submitPerformanceReview(1L));
    }
}
