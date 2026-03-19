package com.revworkforce.controller;

import com.revworkforce.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
@Tag(name = "Reports", description = "Employee report generation and download")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/employee/{employeeId}/download")
    @Operation(summary = "Download employee report", description = "Download employee report as PDF. Admin can download any report, Manager can download reports of their team members, Employee can only download their own report")
    public ResponseEntity<byte[]> downloadEmployeeReport(@PathVariable Long employeeId) {
        try {
            byte[] pdfBytes = reportService.generateEmployeeReport(employeeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "employee_report_" + employeeId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/{employeeId}/pdf")
    @Operation(summary = "Generate employee PDF report", description = "Generate and download comprehensive employee report as PDF")
    public ResponseEntity<byte[]> generateEmployeePDF(@PathVariable Long employeeId) {
        try {
            byte[] pdfBytes = reportService.generateEmployeeReport(employeeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=employee_report_" + employeeId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}