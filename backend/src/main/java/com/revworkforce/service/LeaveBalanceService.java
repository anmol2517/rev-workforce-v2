package com.revworkforce.service;

import com.revworkforce.dto.LeaveBalanceDTO;
import com.revworkforce.entity.LeaveBalance;
import com.revworkforce.entity.LeaveType;
import com.revworkforce.entity.User;
import com.revworkforce.repository.LeaveBalanceRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final AuditLogService auditLogService;

    public LeaveBalance assignLeaveQuota(Long userId, Long leaveTypeId, Integer quota) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        Integer currentYear = Year.now().getValue();
        Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, currentYear);

        LeaveBalance leaveBalance;
        if (existingBalance.isPresent()) {
            leaveBalance = existingBalance.get();
            leaveBalance.setTotalQuota(quota);
            leaveBalance.setRemainingLeaves(quota - leaveBalance.getUsedLeaves());
        } else {
            leaveBalance = new LeaveBalance();
            leaveBalance.setUser(user);
            leaveBalance.setLeaveType(leaveType);
            leaveBalance.setTotalQuota(quota);
            leaveBalance.setUsedLeaves(0);
            leaveBalance.setRemainingLeaves(quota);
            leaveBalance.setYear(currentYear);
        }

        leaveBalance.setUpdatedAt(LocalDateTime.now());
        LeaveBalance savedBalance = leaveBalanceRepository.save(leaveBalance);
        auditLogService.logAction("ASSIGN_QUOTA", "LEAVE_BALANCE", savedBalance.getId(), 
                "Leave quota assigned: " + quota + " days of " + leaveType.getName());
        return savedBalance;
    }

    public List<LeaveBalanceDTO> getEmployeeBalance(Long userId) {
        return leaveBalanceRepository.findByUserId(userId).stream()
                .map(balance -> new LeaveBalanceDTO(
                        balance.getLeaveType().getName(),
                        balance.getTotalQuota(),
                        balance.getUsedLeaves(),
                        balance.getRemainingLeaves()
                ))
                .collect(Collectors.toList());
    }

    public LeaveBalance adjustLeaveBalance(Long userId, Long leaveTypeId, Integer adjustmentDays, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        Integer currentYear = Year.now().getValue();
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, currentYear)
                .orElseThrow(() -> new RuntimeException("Leave balance not found for this user and leave type"));

        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + adjustmentDays);
        leaveBalance.setRemainingLeaves(leaveBalance.getTotalQuota() - leaveBalance.getUsedLeaves());
        leaveBalance.setUpdatedAt(LocalDateTime.now());

        LeaveBalance updatedBalance = leaveBalanceRepository.save(leaveBalance);
        auditLogService.logAction("ADJUST_BALANCE", "LEAVE_BALANCE", updatedBalance.getId(), 
                "Leave balance adjusted: " + adjustmentDays + " days. Reason: " + reason);
        return updatedBalance;
    }

    public List<LeaveBalance> getUserLeaveBalances(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return leaveBalanceRepository.findByUser(user);
    }

    public LeaveBalance getLeaveBalance(Long userId, Long leaveTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        Integer currentYear = Year.now().getValue();
        return leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, currentYear)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
    }

    public void deductLeaves(Long userId, Long leaveTypeId, Integer daysUsed) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        Integer currentYear = Year.now().getValue();
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, currentYear)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (leaveBalance.getRemainingLeaves() < daysUsed) {
            throw new RuntimeException("Insufficient leave balance");
        }

        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + daysUsed);
        leaveBalance.setRemainingLeaves(leaveBalance.getRemainingLeaves() - daysUsed);
        leaveBalance.setUpdatedAt(LocalDateTime.now());
        leaveBalanceRepository.save(leaveBalance);
    }

    @Transactional
    public void assignBulkLeave(Long leaveTypeId, int quota) {
        List<User> users = userRepository.findByRoleIn(List.of(User.UserRole.EMPLOYEE, User.UserRole.MANAGER));
        LeaveType type = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));
        Integer currentYear = Year.now().getValue();

        for (User user : users) {
            if (leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, type, currentYear).isEmpty()) {
                LeaveBalance balance = new LeaveBalance();
                balance.setUser(user);
                balance.setLeaveType(type);
                balance.setTotalQuota(quota);
                balance.setUsedLeaves(0);
                balance.setRemainingLeaves(quota);
                balance.setYear(currentYear);
                balance.setUpdatedAt(LocalDateTime.now());
                leaveBalanceRepository.save(balance);
            }
        }
    }

}