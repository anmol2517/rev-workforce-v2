package com.revworkforce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveBalanceDTO {
    private String leaveType;
    private Integer total;
    private Integer used;
    private Integer remaining;
}