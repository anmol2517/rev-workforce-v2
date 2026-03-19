package com.revworkforce.service;

import com.revworkforce.dto.EmployeeGoalDTO;
import com.revworkforce.entity.EmployeeGoal;
import com.revworkforce.entity.User;
import com.revworkforce.repository.EmployeeGoalRepository;
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
public class EmployeeGoalService {

    private final EmployeeGoalRepository employeeGoalRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public EmployeeGoal createGoal(Long employeeId, EmployeeGoalDTO goalDTO) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeGoal goal = new EmployeeGoal();
        goal.setEmployee(employee);
        goal.setGoalDescription(goalDTO.getGoalDescription());
        goal.setDeadline(goalDTO.getDeadline());
        goal.setPriority(EmployeeGoal.Priority.valueOf(goalDTO.getPriority()));
        goal.setStatus(EmployeeGoal.GoalStatus.NOT_STARTED);
        goal.setProgressPercentage(0);

        EmployeeGoal savedGoal = employeeGoalRepository.save(goal);
        auditLogService.logAction("CREATE", "EMPLOYEE_GOAL", savedGoal.getId(), 
                "Goal created by employee " + employee.getEmployeeId());
        return savedGoal;
    }

    public EmployeeGoal updateGoalProgressWithComment(Long goalId, Integer progressPercentage, String progressComment) {
        EmployeeGoal goal = employeeGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (progressPercentage < 0 || progressPercentage > 100) throw new RuntimeException("Invalid percentage");
        goal.setProgressPercentage(progressPercentage);
        goal.setProgressComment(progressComment);
        if (progressPercentage == 100) goal.setStatus(EmployeeGoal.GoalStatus.COMPLETED);
        else if (progressPercentage > 0) goal.setStatus(EmployeeGoal.GoalStatus.IN_PROGRESS);
        goal.setUpdatedAt(LocalDateTime.now());
        auditLogService.logAction("UPDATE_PROGRESS", "EMPLOYEE_GOAL", goalId, "Progress: " + progressPercentage);
        return employeeGoalRepository.save(goal);
    }

    public void updateGoalProgress(Long goalId, Integer progressPercentage, String status) {
        EmployeeGoal goal = updateGoalProgressWithComment(goalId, progressPercentage, "Status updated to " + status);
        goal.setStatus(EmployeeGoal.GoalStatus.valueOf(status));
        employeeGoalRepository.save(goal);
    }

    public EmployeeGoal updateGoal(Long goalId, EmployeeGoalDTO goalDTO) {
        EmployeeGoal goal = employeeGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setGoalDescription(goalDTO.getGoalDescription());
        goal.setDeadline(goalDTO.getDeadline());
        goal.setPriority(EmployeeGoal.Priority.valueOf(goalDTO.getPriority()));
        goal.setUpdatedAt(LocalDateTime.now());

        EmployeeGoal updatedGoal = employeeGoalRepository.save(goal);
        auditLogService.logAction("UPDATE", "EMPLOYEE_GOAL", goalId, "Goal updated");
        return updatedGoal;
    }

    public void cancelGoal(Long goalId) {
        EmployeeGoal goal = employeeGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setStatus(EmployeeGoal.GoalStatus.CANCELLED);
        goal.setUpdatedAt(LocalDateTime.now());
        employeeGoalRepository.save(goal);

        auditLogService.logAction("CANCEL", "EMPLOYEE_GOAL", goalId, "Goal cancelled");
    }

    public List<EmployeeGoalDTO> getEmployeeGoals(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employeeGoalRepository.findByEmployee(employee)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeGoalDTO> getGoalsByStatus(Long employeeId, String status) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        EmployeeGoal.GoalStatus goalStatus = EmployeeGoal.GoalStatus.valueOf(status);
        return employeeGoalRepository.findByEmployeeAndStatus(employee, goalStatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeGoal getGoalById(Long goalId) {
        return employeeGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    private EmployeeGoalDTO convertToDTO(EmployeeGoal goal) {
        EmployeeGoalDTO dto = new EmployeeGoalDTO();
        dto.setId(goal.getId());
        dto.setEmployeeId(goal.getEmployee().getId());
        dto.setEmployeeName(goal.getEmployee().getFirstName() + " " + goal.getEmployee().getLastName());
        dto.setGoalDescription(goal.getGoalDescription());
        dto.setDeadline(goal.getDeadline());
        dto.setPriority(goal.getPriority().toString());
        dto.setStatus(goal.getStatus().toString());
        dto.setProgressPercentage(goal.getProgressPercentage());
        dto.setProgressComment(goal.getProgressComment());
        dto.setCreatedAt(goal.getCreatedAt());
        dto.setUpdatedAt(goal.getUpdatedAt());
        return dto;
    }

    public EmployeeGoal setGoal(Long employeeId, EmployeeGoalDTO goalDTO) {
        return createGoal(employeeId, goalDTO);
    }

    public List<EmployeeGoal> getMyGoals(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employeeGoalRepository.findByEmployee(employee);
    }

    public EmployeeGoal getGoalByIdEntity(Long goalId) {
        return employeeGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    public List<EmployeeGoal> getTeamGoals() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User manager = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return employeeGoalRepository.findByEmployeeManager(manager);
    }

    public void addManagerComments(Long goalId, String comments) {
        EmployeeGoal goal = getGoalById(goalId);
        goal.setProgressComment(comments);
        employeeGoalRepository.save(goal);
    }


}
