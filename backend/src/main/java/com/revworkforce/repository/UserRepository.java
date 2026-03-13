package com.revworkforce.repository;

import com.revworkforce.entity.Department;
import com.revworkforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department LEFT JOIN FETCH u.designation WHERE u.role = :role")
    List<User> findByRole(@Param("role") User.UserRole role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department LEFT JOIN FETCH u.designation WHERE u.manager = :manager")
    List<User> findByManager(@Param("manager") User manager);
    List<User> findByManagerId(Long managerId);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByActive(String active);
    List<User> findByDepartmentAndRole(Department department, User.UserRole role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department LEFT JOIN FETCH u.designation WHERE (u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.employeeId LIKE %:search%) AND u.active = 'Working'")
    List<User> searchEmployees(@Param("search") String search);
    List<User> findByRoleIn(List<User.UserRole> roles);
    long countByRole(User.UserRole role);
    Optional<User> findFirstByRoleAndDepartmentId(User.UserRole role, Long departmentId);
    Optional<User> findFirstByRoleAndDepartmentIdAndIdNot(User.UserRole role, Long departmentId, Long id);
}