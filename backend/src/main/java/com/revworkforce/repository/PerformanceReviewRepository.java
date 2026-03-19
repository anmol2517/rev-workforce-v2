package com.revworkforce.repository;

import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployee(User employee);
    List<PerformanceReview> findByManager(User manager);

    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.manager = :manager AND pr.status = 'SUBMITTED'")
    List<PerformanceReview> findSubmittedReviewsByManager(@Param("manager") User manager);
}
