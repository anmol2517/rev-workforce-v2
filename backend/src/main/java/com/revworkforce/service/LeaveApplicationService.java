package com.revworkforce.service;

import com.revworkforce.dto.LeaveApplicationDTO;
import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.LeaveType;
import com.revworkforce.entity.User;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public LeaveApplication applyForLeave(Long userId, LeaveApplicationDTO leaveDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveDTO.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setUser(user);
        leaveApplication.setLeaveType(leaveType);
        leaveApplication.setStartDate(leaveDTO.getStartDate());
        leaveApplication.setEndDate(leaveDTO.getEndDate());
        leaveApplication.setReason(leaveDTO.getReason());
        leaveApplication.setStatus(LeaveApplication.LeaveStatus.PENDING);
        leaveApplication.setAppliedDate(LocalDateTime.now());

        LeaveApplication savedApplication = leaveApplicationRepository.save(leaveApplication);

        if (user.getManager() != null) {
            notificationService.createNotification(
                    user.getManager(),
                    "Leave Application",
                    user.getFirstName() + " " + user.getLastName() + " has applied for leave",
                    "LEAVE_APPLIED",
                    "LEAVE_APPLICATION",
                    savedApplication.getId()
            );
        }

        auditLogService.logAction("APPLY", "LEAVE_APPLICATION", savedApplication.getId(),
                "Leave application created by " + user.getEmployeeId());

        return savedApplication;
    }

    public LeaveApplication approveLeave(Long leaveApplicationId, String comment) {

        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveApplicationId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));

        if (!leaveApplication.getStatus().equals(LeaveApplication.LeaveStatus.PENDING)) {
            throw new RuntimeException("Only pending leaves can be approved");
        }

        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        leaveApplication.setStatus(LeaveApplication.LeaveStatus.APPROVED);
        leaveApplication.setApprovedBy(currentUser);
        leaveApplication.setApprovalComment(comment);
        leaveApplication.setUpdatedAt(LocalDateTime.now());

        LeaveApplication updatedApplication = leaveApplicationRepository.save(leaveApplication);

        leaveBalanceService.deductLeaves(
                leaveApplication.getUser().getId(),
                leaveApplication.getLeaveType().getId(),
                calculateDaysBetween(leaveApplication.getStartDate(), leaveApplication.getEndDate())
        );

        notificationService.createNotification(
                leaveApplication.getUser(),
                "Leave Approved",
                "Your leave application has been approved",
                "LEAVE_APPROVED",
                "LEAVE_APPLICATION",
                leaveApplicationId
        );

        auditLogService.logAction("APPROVE", "LEAVE_APPLICATION", leaveApplicationId,
                "Leave application approved. Comment: " + comment);

        return updatedApplication;
    }

    public LeaveApplication rejectLeave(Long leaveApplicationId, String comment) {

        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveApplicationId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));

        if (!leaveApplication.getStatus().equals(LeaveApplication.LeaveStatus.PENDING)) {
            throw new RuntimeException("Only pending leaves can be rejected");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Comment is mandatory for rejection");
        }

        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        leaveApplication.setStatus(LeaveApplication.LeaveStatus.REJECTED);
        leaveApplication.setApprovedBy(currentUser);
        leaveApplication.setApprovalComment(comment);
        leaveApplication.setUpdatedAt(LocalDateTime.now());

        LeaveApplication updatedApplication = leaveApplicationRepository.save(leaveApplication);

        notificationService.createNotification(
                leaveApplication.getUser(),
                "Leave Rejected",
                "Your leave application has been rejected. Reason: " + comment,
                "LEAVE_REJECTED",
                "LEAVE_APPLICATION",
                leaveApplicationId
        );

        auditLogService.logAction("REJECT", "LEAVE_APPLICATION", leaveApplicationId,
                "Leave application rejected. Comment: " + comment);

        return updatedApplication;
    }

    public void cancelLeave(Long leaveApplicationId) {

        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveApplicationId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));

        if (!leaveApplication.getStatus().equals(LeaveApplication.LeaveStatus.PENDING)) {
            throw new RuntimeException("Only pending leaves can be cancelled");
        }

        leaveApplication.setStatus(LeaveApplication.LeaveStatus.CANCELLED);
        leaveApplication.setUpdatedAt(LocalDateTime.now());
        leaveApplicationRepository.save(leaveApplication);

        auditLogService.logAction("CANCEL", "LEAVE_APPLICATION", leaveApplicationId,
                "Leave application cancelled by employee");
    }

    public List<LeaveApplicationDTO> getUserLeaveApplications(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return leaveApplicationRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LeaveApplicationDTO getLeaveApplicationById(Long applicationId) {
        LeaveApplication application = leaveApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));
        return convertToDTO(application);
    }

    public List<LeaveApplicationDTO> getTeamPendingLeaves() {

        User manager = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return leaveApplicationRepository.findPendingLeavesByManager(manager)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationDTO> getApprovedLeavesByManager() {

        User manager = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return leaveApplicationRepository.findByApprovedBy(manager)
                .stream()
                .filter(la -> la.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private int calculateDaysBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private LeaveApplicationDTO convertToDTO(LeaveApplication leaveApplication) {

        LeaveApplicationDTO dto = new LeaveApplicationDTO();
        dto.setId(leaveApplication.getId());
        dto.setUserId(leaveApplication.getUser().getId());
        dto.setEmployeeName(leaveApplication.getUser().getFirstName() + " " + leaveApplication.getUser().getLastName());
        dto.setLeaveTypeId(leaveApplication.getLeaveType().getId());
        dto.setLeaveTypeName(leaveApplication.getLeaveType().getName());
        dto.setStartDate(leaveApplication.getStartDate());
        dto.setEndDate(leaveApplication.getEndDate());
        dto.setReason(leaveApplication.getReason());
        dto.setStatus(leaveApplication.getStatus().toString());

        if (leaveApplication.getApprovedBy() != null) {
            dto.setApprovedBy(leaveApplication.getApprovedBy().getId());
            dto.setApproverName(leaveApplication.getApprovedBy().getFirstName() + " " + leaveApplication.getApprovedBy().getLastName());
        }

        dto.setApprovalComment(leaveApplication.getApprovalComment());
        dto.setAppliedDate(leaveApplication.getAppliedDate());
        dto.setCreatedAt(leaveApplication.getCreatedAt());
        dto.setUpdatedAt(leaveApplication.getUpdatedAt());

        return dto;
    }

    public Map<String, Object> getLeaveUtilizationStats() {
        List<LeaveApplication> allApproved = leaveApplicationRepository.findAll().stream()
                .filter(la -> la.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                .toList();

        Map<String, Long> departmentWise = allApproved.stream()
                .collect(Collectors.groupingBy(la -> la.getUser().getDepartmentName(), Collectors.counting()));

        Map<String, Long> typeWise = allApproved.stream()
                .collect(Collectors.groupingBy(la -> la.getLeaveType().getName(), Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLeavesTaken", allApproved.size());
        stats.put("departmentUtilization", departmentWise);
        stats.put("leaveTypeUtilization", typeWise);
        return stats;
    }

    public List<LeaveApplicationDTO> getAllEmployeeLeaves() {
        return leaveApplicationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDynamicTeamCalendar(Long managerId) {
        List<User> team = userRepository.findByManagerId(managerId);

        return team.stream().map(member -> {
            Map<String, Object> record = new HashMap<>();
            record.put("employeeName", member.getFirstName() + " " + member.getLastName());

            List<LeaveApplicationDTO> approvedLeaves = leaveApplicationRepository.findByUser(member)
                    .stream()
                    .filter(la -> la.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            record.put("approvedLeaves", approvedLeaves);
            return record;
        }).collect(Collectors.toList());
    }

}