package com.revworkforce.repository;

import com.revworkforce.entity.ManagerFeedback;
import com.revworkforce.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerFeedbackRepository extends JpaRepository<ManagerFeedback, Long> {
    Optional<ManagerFeedback> findByPerformanceReview(PerformanceReview performanceReview);
}
