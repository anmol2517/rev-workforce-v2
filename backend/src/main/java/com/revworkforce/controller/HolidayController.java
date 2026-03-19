package com.revworkforce.controller;

import com.revworkforce.entity.Holiday;
import com.revworkforce.service.HolidayService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/holidays")
@AllArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping("/add")
    public ResponseEntity<?> addHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Holiday added successfully",
                "data", holidayService.createHoliday(holiday)
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", holidayService.getAllHolidays()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Holiday holiday) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", holidayService.updateHoliday(id, holiday)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Holiday deleted successfully"
        ));
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> addHolidaysBulk(@RequestBody java.util.List<Holiday> holidays) {
        for (Holiday h : holidays) {
            holidayService.createHoliday(h);
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", holidays.size() + " Holidays added successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHolidayById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", holidayService.getHolidayById(id)
        ));
    }
}