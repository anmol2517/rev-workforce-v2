package com.revworkforce.service;

import com.revworkforce.entity.Department;
import com.revworkforce.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    public Department createDepartment(Department department) {
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            throw new RuntimeException("Department with this name already exists");
        }
        Department savedDepartment = departmentRepository.save(department);
        auditLogService.logAction("CREATE", "DEPARTMENT", savedDepartment.getId(), "Department created: " + department.getName());
        return savedDepartment;
    }

    public Department updateDepartment(Long departmentId, Department departmentDetails) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!department.getName().equals(departmentDetails.getName())) {
            if (departmentRepository.findByName(departmentDetails.getName()).isPresent()) {
                throw new RuntimeException("Department with this name already exists");
            }
        }

        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        department.setUpdatedAt(LocalDateTime.now());

        Department updatedDepartment = departmentRepository.save(department);
        auditLogService.logAction("UPDATE", "DEPARTMENT", departmentId, "Department updated: " + department.getName());
        return updatedDepartment;
    }

    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new RuntimeException("Cannot delete department: " + department.getEmployees().size() + " employee(s) are assigned to this department. Reassign them first.");
        }

        String deptName = department.getName();
        auditLogService.logAction("DELETE", "DEPARTMENT", departmentId, "Department deleted: " + deptName);
        departmentRepository.delete(department);
    }

    public Department getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> createAllDepartments(List<Department> departments) {
        List<Department> savedDepartments = departmentRepository.saveAll(departments);
        for (Department dept : savedDepartments) {
            auditLogService.logAction("CREATE", "DEPARTMENT", dept.getId(), "Bulk Department created: " + dept.getName());
        }
        return savedDepartments;
    }
}