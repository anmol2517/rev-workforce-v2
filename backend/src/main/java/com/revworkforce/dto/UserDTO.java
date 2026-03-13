package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String employeeId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private Long managerId;
    private String managerName;
    private Double salary;
    private String phoneNumber;
    private String address;
    private LocalDateTime joiningDate;
    private String active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String password;
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
