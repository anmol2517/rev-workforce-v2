package com.revworkforce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "performance_review_id")
    private PerformanceReview performanceReview;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String feedback = "";

    @Column(nullable = false)
    private Integer managerRating = 0;

    @Column(nullable = false)
    private LocalDateTime submittedDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
