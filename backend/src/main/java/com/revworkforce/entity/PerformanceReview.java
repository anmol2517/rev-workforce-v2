package com.revworkforce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String keyDeliverables = "";

    @Column(columnDefinition = "TEXT", nullable = false)
    private String accomplishments = "";

    @Column(columnDefinition = "TEXT", nullable = false)
    private String areasOfImprovement = "";

    @Column(nullable = false)
    private Integer employeeSelfRating = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime submittedDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ReviewStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, COMPLETED
    }
}
