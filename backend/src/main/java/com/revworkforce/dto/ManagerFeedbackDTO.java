package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerFeedbackDTO {
    private Long id;
    private Long performanceReviewId;
    private Long managerId;
    private String managerName;
    private String feedback;
    private Integer managerRating;
    private LocalDateTime submittedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
