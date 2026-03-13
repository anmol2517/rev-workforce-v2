package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeGoalDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String goalDescription;
    private LocalDate deadline;
    private String priority;
    private String status;
    private Integer progressPercentage;
    private String progressComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
