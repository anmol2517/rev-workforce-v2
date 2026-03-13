package com.revworkforce.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HolidayDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate holidayDate;
    private Boolean isOptional;
}