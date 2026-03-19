package com.revworkforce.config;

import com.revworkforce.entity.Department;
import com.revworkforce.entity.User;
import com.revworkforce.repository.DepartmentRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

@Configuration
@AllArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return args -> {
            String adminEmail = System.getenv("ADMIN_EMAIL");
            String rawPassword = System.getenv("ADMIN_PASSWORD");
            if (adminEmail == null || rawPassword == null || rawPassword.isBlank()) {
                return;
            }
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmployeeId("SYS-001");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(System.getenv("ADMIN_PASSWORD")));
                admin.setRole(User.UserRole.ADMIN);
                admin.setFirstName(System.getenv("ADMIN_FNAME"));
                admin.setLastName(System.getenv("ADMIN_LNAME"));
                admin.setPhoneNumber(System.getenv("ADMIN_PHONE"));
                admin.setAddress(System.getenv("ADMIN_ADDR"));
                admin.setActive(User.STATUS_WORKING);
                userRepository.save(admin);
            }
        };
    }
}
