package com.revworkforce.repository;

import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByUser(User user);
    List<LeaveApplication> findByApprovedBy(User manager);
    
    @Query("SELECT la FROM LeaveApplication la WHERE la.user IN (SELECT u FROM User u WHERE u.manager = :manager) AND la.status = 'PENDING'")
    List<LeaveApplication> findPendingLeavesByManager(@Param("manager") User manager);
    
    @Query("SELECT la FROM LeaveApplication la WHERE la.user = :user AND la.status = 'PENDING'")
    List<LeaveApplication> findPendingLeavesByUser(@Param("user") User user);
    
    @Query("SELECT la FROM LeaveApplication la WHERE la.startDate <= :date AND la.endDate >= :date AND la.status = 'APPROVED'")
    List<LeaveApplication> findLeavesByDate(@Param("date") LocalDate date);
    Long countByStatus(com.revworkforce.entity.LeaveApplication.LeaveStatus status);
}
