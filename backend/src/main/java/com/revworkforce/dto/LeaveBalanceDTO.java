package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceDTO {
    private Long leaveTypeId;
    private Integer quota;
    private String leaveType;
    private Integer total;
    private Integer used;
    private Integer remaining;
}