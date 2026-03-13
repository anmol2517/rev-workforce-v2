package com.revworkforce.service;

import com.revworkforce.entity.LeaveType;
import com.revworkforce.repository.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final AuditLogService auditLogService;

    public LeaveType createLeaveType(LeaveType leaveType) {
        if (leaveTypeRepository.findByName(leaveType.getName()).isPresent()) {
            throw new RuntimeException("Leave type with this name already exists");
        }
        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);
        auditLogService.logAction("CREATE", "LEAVE_TYPE", savedLeaveType.getId(), "Leave type created: " + leaveType.getName());
        return savedLeaveType;
    }

    public LeaveType updateLeaveType(Long leaveTypeId, LeaveType leaveTypeDetails) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        if (!leaveType.getName().equals(leaveTypeDetails.getName())) {
            if (leaveTypeRepository.findByName(leaveTypeDetails.getName()).isPresent()) {
                throw new RuntimeException("Leave type with this name already exists");
            }
        }

        leaveType.setName(leaveTypeDetails.getName());
        leaveType.setDescription(leaveTypeDetails.getDescription());
        leaveType.setDefaultQuota(leaveTypeDetails.getDefaultQuota());
        leaveType.setIsActive(leaveTypeDetails.getIsActive());
        leaveType.setUpdatedAt(LocalDateTime.now());

        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);
        auditLogService.logAction("UPDATE", "LEAVE_TYPE", leaveTypeId, "Leave type updated: " + leaveType.getName());
        return updatedLeaveType;
    }

    public void deleteLeaveType(Long leaveTypeId) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));
        leaveTypeRepository.delete(leaveType);
        auditLogService.logAction("DELETE", "LEAVE_TYPE", leaveTypeId, "Leave type deleted: " + leaveType.getName());
    }

    public LeaveType getLeaveTypeById(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));
    }

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public List<LeaveType> getActiveLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .filter(LeaveType::getIsActive)
                .toList();
    }

    public List<LeaveType> saveAll(List<LeaveType> types) {
        List<LeaveType> savedTypes = leaveTypeRepository.saveAll(types);
        auditLogService.logAction("BULK_CREATE", "LEAVE_TYPE", 0L, "Bulk created " + types.size() + " leave types");
        return savedTypes;
    }
}
