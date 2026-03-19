package com.revworkforce.controller;

import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.repository.DepartmentRepository;
import com.revworkforce.repository.UserRepository;
import com.revworkforce.repository.LeaveApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@AllArgsConstructor
public class DashboardController {
    private final UserRepository userRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/main/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalEmployees", userRepository.count(),
                "totalDepartments", departmentRepository.count(),
                "pendingLeaves", leaveRepository.countByStatus(LeaveApplication.LeaveStatus.PENDING),
                "approvedLeaves", leaveRepository.countByStatus(LeaveApplication.LeaveStatus.APPROVED)
        ));
    }
}