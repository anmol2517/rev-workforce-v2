package com.revworkforce.service;

import com.revworkforce.dto.UserDTO;
import com.revworkforce.entity.Department;
import com.revworkforce.entity.Designation;
import com.revworkforce.entity.User;
import com.revworkforce.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Primary
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new RuntimeException("Unable to get current user");
    }

    public User addEmployee(UserDTO userDTO) {
        User.UserRole selectedRole = User.UserRole.valueOf(userDTO.getRole().toUpperCase());
        String firstName = userDTO.getFirstName();
        String email = userDTO.getEmail() != null ? userDTO.getEmail() : firstName.toLowerCase() + "@revworkforce.com";

        User user = new User();
        user.setRole(selectedRole);

        String prefix = (selectedRole == User.UserRole.ADMIN) ? "SYS-" :
                (selectedRole == User.UserRole.MANAGER) ? "MGR-" : "EMP-";
        long count = userRepository.countByRole(selectedRole) + 1;
        user.setEmployeeId(prefix + String.format("%03d", count));

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(firstName + "@123"));
        user.setFirstName(firstName);
        user.setLastName(userDTO.getLastName());
        user.setSalary(userDTO.getSalary());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setJoiningDate(userDTO.getJoiningDate());
        user.setActive(User.STATUS_WORKING);

        if (userDTO.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(dept);
            user.setDepartmentName(dept.getName());
        }

        if (userDTO.getDesignationId() != null) {
            Designation desg = designationRepository.findById(userDTO.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
            user.setDesignation(desg);
            user.setDesignationName(desg.getName());
        }

        if (selectedRole == User.UserRole.EMPLOYEE && user.getDepartment() != null) {
            userRepository.findFirstByRoleAndDepartmentId(User.UserRole.MANAGER, user.getDepartment().getId())
                    .ifPresent(user::setManager);
        } else {
            user.setManager(null);
        }

        User savedUser = userRepository.save(user);
        auditLogService.logAction("CREATE", "USER", savedUser.getId(), "User created: " + savedUser.getEmployeeId());
        return savedUser;
    }

    public User updateEmployee(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(userDTO.getEmail())) {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setSalary(userDTO.getSalary());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());

        if (userDTO.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(dept);
            user.setDepartmentName(dept.getName());
        }

        if (userDTO.getDesignationId() != null) {
            Designation desg = designationRepository.findById(userDTO.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
            user.setDesignation(desg);
            user.setDesignationName(desg.getName());
        }

        User.UserRole selectedRole = User.UserRole.valueOf(userDTO.getRole().toUpperCase());
        user.setRole(selectedRole);

        if (selectedRole == User.UserRole.EMPLOYEE) {
            if (user.getDepartment() != null) {
                userRepository.findFirstByRoleAndDepartmentIdAndIdNot(User.UserRole.MANAGER, user.getDepartment().getId(), userId)
                        .ifPresentOrElse(user::setManager, () -> user.setManager(null));
            }
        } else {
            user.setManager(null);
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        auditLogService.logAction("UPDATE", "USER", userId, "Employee updated: " + user.getEmployeeId());
        return updatedUser;
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO getUserProfile() {
        User user = getCurrentUser();
        return convertToDTO(user);
    }

    public UserDTO getReportingManager() {
        User currentUser = getCurrentUser();
        if (currentUser.getManager() == null) {
            throw new RuntimeException("Reporting manager not assigned");
        }
        return convertToDTO(currentUser.getManager());
    }

    public List<UserDTO> getAllEmployees() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != User.UserRole.ADMIN)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchEmployees(String search) {
        return userRepository.searchEmployees(search)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getTeamMembers() {
        User manager = getCurrentUser();
        return userRepository.findByManager(manager)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deactivateEmployee(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(User.STATUS_LEFT);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogService.logAction("DEACTIVATE", "USER", userId, "Employee deactivated: " + user.getEmployeeId());
    }

    public void reactivateEmployee(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(User.STATUS_WORKING);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogService.logAction("REACTIVATE", "USER", userId, "Employee reactivated: " + user.getEmployeeId());
    }

    public void assignManager(Long employeeId, Long managerId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        employee.setManager(manager);
        employee.setUpdatedAt(LocalDateTime.now());
        userRepository.save(employee);
        auditLogService.logAction("ASSIGN_MANAGER", "USER", employeeId, "Manager assigned to employee");
    }

    public void updateProfile(UserDTO userDTO) {
        User user = getCurrentUser();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogService.logAction("UPDATE_PROFILE", "USER", user.getId(), "User profile updated");
    }

    public void deleteEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        String empId = user.getEmployeeId();
        auditLogService.logAction("DELETE", "USER", id, "Employee deleted: " + empId);
        userRepository.deleteById(id);
    }

    public List<User> addEmployeesBulk(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(this::addEmployee)
                .collect(Collectors.toList());
    }

    public UserDTO getTeamMemberDetails(Long employeeId, Long managerId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (employee.getManager() == null || !employee.getManager().getId().equals(managerId)) {
            throw new RuntimeException("Unauthorized: You are not the manager of this employee");
        }
        return convertToDTO(employee);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().toString());
        dto.setSalary(user.getSalary());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setJoiningDate(user.getJoiningDate());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getDepartment() != null) {
            dto.setDepartmentId(user.getDepartment().getId());
            dto.setDepartmentName(user.getDepartment().getName());
        }
        if (user.getDesignation() != null) {
            dto.setDesignationId(user.getDesignation().getId());
            dto.setDesignationName(user.getDesignation().getName());
        }
        if (user.getManager() != null) {
            dto.setManagerId(user.getManager().getId());
            dto.setManagerName(user.getManager().getFirstName() + " " + user.getManager().getLastName());
        }
        return dto;
    }

    public String generateUniqueEmail(String firstName) {
        String baseEmail = firstName.toLowerCase() + "@revworkforce.com";
        String email = baseEmail;
        int count = 1;
        while (userRepository.findByEmail(email).isPresent()) {
            email = firstName.toLowerCase() + count + "@revworkforce.com";
            count++;
        }
        return email;
    }
}