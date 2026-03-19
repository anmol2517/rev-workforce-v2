package com.revworkforce.service;

import com.revworkforce.entity.Holiday;
import com.revworkforce.repository.HolidayRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final AuditLogService auditLogService;

    public Holiday createHoliday(Holiday holiday) {
        if (holidayRepository.findByHolidayDate(holiday.getHolidayDate()).isPresent()) {
            throw new RuntimeException("Holiday already exists on this date");
        }
        Holiday savedHoliday = holidayRepository.save(holiday);
        auditLogService.logAction("CREATE", "HOLIDAY", savedHoliday.getId(), "Holiday created: " + holiday.getName());
        return savedHoliday;
    }

    public Holiday updateHoliday(Long holidayId, Holiday holidayDetails) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        if (!holiday.getHolidayDate().equals(holidayDetails.getHolidayDate())) {
            if (holidayRepository.findByHolidayDate(holidayDetails.getHolidayDate()).isPresent()) {
                throw new RuntimeException("Holiday already exists on this date");
            }
        }

        holiday.setName(holidayDetails.getName());
        holiday.setDescription(holidayDetails.getDescription());
        holiday.setHolidayDate(holidayDetails.getHolidayDate());
        holiday.setIsOptional(holidayDetails.getIsOptional());
        holiday.setUpdatedAt(LocalDateTime.now());

        Holiday updatedHoliday = holidayRepository.save(holiday);
        auditLogService.logAction("UPDATE", "HOLIDAY", holidayId, "Holiday updated: " + holiday.getName());
        return updatedHoliday;
    }

    public void deleteHoliday(Long holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));
        holidayRepository.delete(holiday);
        auditLogService.logAction("DELETE", "HOLIDAY", holidayId, "Holiday deleted: " + holiday.getName());
    }

    public Holiday getHolidayById(Long holidayId) {
        return holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));
    }

    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAllByOrderByHolidayDateAsc();
    }

    public boolean isHoliday(LocalDate date) {
        return holidayRepository.findByHolidayDate(date).isPresent();
    }

    public long getUpcomingHolidaysCount() {
        return holidayRepository.findAll().stream()
                .filter(h -> h.getHolidayDate().isAfter(LocalDate.now()))
                .count();
    }
}
