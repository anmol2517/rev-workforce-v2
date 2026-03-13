package com.revworkforce.repository;

import com.revworkforce.entity.EmployeeGoal;
import com.revworkforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Long> {
    List<EmployeeGoal> findByEmployee(User employee);
    List<EmployeeGoal> findByEmployeeAndStatus(User employee, EmployeeGoal.GoalStatus status);

    //  List<EmployeeGoal> findByUser(User user);
    //  List<EmployeeGoal> findByEmployee_Manager(User manager);

    List<EmployeeGoal> findByEmployeeManager(User manager);
    long countByStatus(com.revworkforce.entity.EmployeeGoal.GoalStatus status);
}
