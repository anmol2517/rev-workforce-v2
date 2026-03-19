package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationDTO {
    private Long id;
    private Long userId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private Long approvedBy;
    private String approverName;
    private String approvalComment;
    private LocalDateTime appliedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
