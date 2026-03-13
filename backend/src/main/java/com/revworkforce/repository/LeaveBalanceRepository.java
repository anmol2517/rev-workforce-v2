package com.revworkforce.repository;

import com.revworkforce.entity.LeaveBalance;
import com.revworkforce.entity.User;
import com.revworkforce.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, Integer year);
    List<LeaveBalance> findByUser(User user);
    List<LeaveBalance> findByUserId(Long userId);
}
