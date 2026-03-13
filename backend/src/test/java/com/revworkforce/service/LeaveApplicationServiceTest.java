package com.revworkforce.service;

import com.revworkforce.dto.LeaveApplicationDTO;
import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.LeaveType;
import com.revworkforce.entity.User;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import com.revworkforce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveApplicationServiceTest {

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private LeaveApplicationService leaveApplicationService;

    private User testEmployee;
    private LeaveType testLeaveType;
    private LeaveApplication testLeaveApplication;
    private LeaveApplicationDTO testLeaveDTO;

    @BeforeEach
    public void setUp() {
        testEmployee = new User();
        testEmployee.setId(1L);
        testEmployee.setEmployeeId("EMP001");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");

        testLeaveType = new LeaveType();
        testLeaveType.setId(1L);
        testLeaveType.setName("Casual Leave");
        testLeaveType.setDefaultQuota(10);

        testLeaveDTO = new LeaveApplicationDTO();
        testLeaveDTO.setLeaveTypeId(1L);
        testLeaveDTO.setStartDate(LocalDate.now().plusDays(1));
        testLeaveDTO.setEndDate(LocalDate.now().plusDays(3));
        testLeaveDTO.setReason("Personal work");

        testLeaveApplication = new LeaveApplication();
        testLeaveApplication.setId(1L);
        testLeaveApplication.setUser(testEmployee);
        testLeaveApplication.setLeaveType(testLeaveType);
        testLeaveApplication.setStartDate(testLeaveDTO.getStartDate());
        testLeaveApplication.setEndDate(testLeaveDTO.getEndDate());
        testLeaveApplication.setReason(testLeaveDTO.getReason());
        testLeaveApplication.setStatus(LeaveApplication.LeaveStatus.PENDING);
    }

    @Test
    public void testApplyForLeave_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(testLeaveApplication);

        LeaveApplication result = leaveApplicationService.applyForLeave(1L, testLeaveDTO);

        assertNotNull(result);
        assertEquals(LeaveApplication.LeaveStatus.PENDING, result.getStatus());
        assertEquals(testLeaveDTO.getReason(), result.getReason());
        verify(leaveApplicationRepository, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    public void testApplyForLeave_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> leaveApplicationService.applyForLeave(999L, testLeaveDTO));
    }

    @Test
    public void testCancelLeave_Success() {
        testLeaveApplication.setStatus(LeaveApplication.LeaveStatus.PENDING);
        when(leaveApplicationRepository.findById(1L)).thenReturn(Optional.of(testLeaveApplication));
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(testLeaveApplication);

        leaveApplicationService.cancelLeave(1L);

        assertEquals(LeaveApplication.LeaveStatus.CANCELLED, testLeaveApplication.getStatus());
        verify(leaveApplicationRepository, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    public void testCancelLeave_NotPending() {
        testLeaveApplication.setStatus(LeaveApplication.LeaveStatus.APPROVED);
        when(leaveApplicationRepository.findById(1L)).thenReturn(Optional.of(testLeaveApplication));

        assertThrows(RuntimeException.class, () -> leaveApplicationService.cancelLeave(1L));
    }
}
