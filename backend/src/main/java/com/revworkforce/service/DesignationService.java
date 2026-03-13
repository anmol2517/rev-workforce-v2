package com.revworkforce.service;

import com.revworkforce.entity.Designation;
import com.revworkforce.repository.DesignationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class DesignationService {

    private final DesignationRepository designationRepository;
    private final AuditLogService auditLogService;

    public Designation createDesignation(Designation designation) {
        if (designationRepository.findByName(designation.getName()).isPresent()) {
            throw new RuntimeException("Designation with this name already exists");
        }
        Designation savedDesignation = designationRepository.save(designation);
        auditLogService.logAction("CREATE", "DESIGNATION", savedDesignation.getId(), "Designation created: " + designation.getName());
        return savedDesignation;
    }

    public Designation updateDesignation(Long designationId, Designation designationDetails) {
        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        if (!designation.getName().equals(designationDetails.getName())) {
            if (designationRepository.findByName(designationDetails.getName()).isPresent()) {
                throw new RuntimeException("Designation with this name already exists");
            }
        }

        designation.setName(designationDetails.getName());
        designation.setDescription(designationDetails.getDescription());
        designation.setUpdatedAt(LocalDateTime.now());

        Designation updatedDesignation = designationRepository.save(designation);
        auditLogService.logAction("UPDATE", "DESIGNATION", designationId, "Designation updated: " + designation.getName());
        return updatedDesignation;
    }

    public void deleteDesignation(Long designationId) {
        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException("Designation not found"));
        designationRepository.delete(designation);
        auditLogService.logAction("DELETE", "DESIGNATION", designationId, "Designation deleted: " + designation.getName());
    }

    public Designation getDesignationById(Long designationId) {
        return designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException("Designation not found"));
    }

    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }

    public List<Designation> createAllDesignations(List<Designation> designations) {
        List<Designation> savedDesignations = designationRepository.saveAll(designations);
        for (Designation desig : savedDesignations) {
            auditLogService.logAction("CREATE", "DESIGNATION", desig.getId(), "Bulk Designation created: " + desig.getName());
        }
        return savedDesignations;
    }
}
