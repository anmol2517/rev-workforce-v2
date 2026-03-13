package com.revworkforce.repository;

import com.revworkforce.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    long countByStatus(String status);

    // @Query("SELECT COUNT(l) FROM LeaveRequest l WHERE l.status = 'APPROVED' AND :today BETWEEN l.startDate AND l.endDate")

    @Query("SELECT COUNT(l) FROM LeaveRequest l WHERE l.status = 'APPROVED' AND :today BETWEEN l.startDate AND l.endDate")
    long countActiveLeaves(@Param("today") LocalDate today);
}