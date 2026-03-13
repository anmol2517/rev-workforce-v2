package com.revworkforce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer totalQuota = 0;

    @Column(nullable = false)
    private Integer usedLeaves = 0;

    @Column(nullable = false)
    private Integer remainingLeaves = 0;

    @Column(nullable = false)
    private Integer year = Year.now().getValue();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Table(name = "leave_balances", uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "leave_type_id", "year"})
    })
    public static class LeaveBalanceConstraints {
    }
}
