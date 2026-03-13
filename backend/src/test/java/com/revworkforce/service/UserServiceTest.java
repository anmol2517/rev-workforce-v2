package com.revworkforce.service;

import com.revworkforce.dto.UserDTO;
import com.revworkforce.entity.Department;
import com.revworkforce.entity.Designation;
import com.revworkforce.entity.User;
import com.revworkforce.repository.DepartmentRepository;
import com.revworkforce.repository.DesignationRepository;
import com.revworkforce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Department testDepartment;
    private Designation testDesignation;

    @BeforeEach
    public void setUp() {
        testDepartment = new Department(1L, "IT", "Information Technology", null, null, null);
        testDesignation = new Designation(1L, "Software Engineer", "Software Development", null, null, null);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmployeeId("EMP001");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.UserRole.EMPLOYEE);
        testUser.setDepartment(testDepartment);
        testUser.setDesignation(testDesignation);
        testUser.setActive(User.STATUS_WORKING);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setEmployeeId("EMP001");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setFirstName("John");
        testUserDTO.setLastName("Doe");
        testUserDTO.setDepartmentId(1L);
        testUserDTO.setDesignationId(1L);
    }

    @Test
    public void testAddEmployee_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmployeeId(testUserDTO.getEmployeeId())).thenReturn(Optional.empty());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(designationRepository.findById(1L)).thenReturn(Optional.of(testDesignation));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.addEmployee(testUserDTO);

        assertNotNull(result);
        assertEquals(testUser.getEmployeeId(), result.getEmployeeId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogService, times(1)).logAction(any(), any(), any(), any());
    }

    @Test
    public void testAddEmployee_DuplicateEmail() {
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> userService.addEmployee(testUserDTO));
    }

    @Test
    public void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
    }

    @Test
    public void testDeactivateEmployee_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateEmployee(1L);

        assertEquals(User.STATUS_LEFT, testUser.getActive());
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogService, times(1)).logAction(any(), any(), any(), any());
    }
}
