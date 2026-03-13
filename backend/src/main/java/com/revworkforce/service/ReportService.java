package com.revworkforce.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.revworkforce.entity.*;
import com.revworkforce.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ReportService {

    private final UserRepository userRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeGoalRepository employeeGoalRepository;
    private final AuditLogService auditLogService;

    public byte[] generateEmployeeReport(Long employeeId) throws DocumentException {
        User loggedInUser = getCurrentUser();
        User targetEmployee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!canAccessEmployeeReport(loggedInUser, targetEmployee)) {
            throw new RuntimeException("You don't have permission to download this report");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        document.add(new Paragraph("Employee Report", titleFont));
        document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                new Font(Font.FontFamily.HELVETICA, 10)));
        document.add(new Paragraph(" "));

        addEmployeeDetailsSection(document, targetEmployee);
        addLeaveSummarySection(document, targetEmployee);
        addPerformanceSection(document, targetEmployee);
        addGoalsSection(document, targetEmployee);

        document.close();

        auditLogService.logAction("DOWNLOAD", "REPORT", employeeId,
                "Employee report downloaded for: " + targetEmployee.getFirstName() + " " + targetEmployee.getLastName());

        return outputStream.toByteArray();
    }

    private void addEmployeeDetailsSection(Document document, User employee) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        document.add(new Paragraph("Employee Details", sectionFont));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Employee ID:", employee.getId().toString());
        addTableRow(table, "Name:", employee.getFirstName() + " " + employee.getLastName());
        addTableRow(table, "Email:", employee.getEmail());
        addTableRow(table, "Phone:", employee.getPhoneNumber());
        addTableRow(table, "Department:", employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A");
        addTableRow(table, "Designation:", employee.getDesignation() != null ? employee.getDesignation().getName() : "N/A");
        addTableRow(table, "Joining Date:", employee.getJoiningDate().toString());
        addTableRow(table, "Status:", "Working".equals(employee.getActive()) ? "Active" : "Inactive");
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addLeaveSummarySection(Document document, User employee) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        document.add(new Paragraph("Leave Summary", sectionFont));

        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findByUser(employee);

        long approved = leaveApplications.stream()
                .filter(l -> "APPROVED".equals(String.valueOf(l.getStatus())))
                .mapToLong(l -> ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();

        long pending = leaveApplications.stream()
                .filter(l -> "PENDING".equals(String.valueOf(l.getStatus())))
                .count();

        long rejected = leaveApplications.stream()
                .filter(l -> "REJECTED".equals(String.valueOf(l.getStatus())))
                .count();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Total Approved Leaves:", approved + " days");
        addTableRow(table, "Pending Requests:", pending + "");
        addTableRow(table, "Rejected Requests:", rejected + "");

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addPerformanceSection(Document document, User employee) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        document.add(new Paragraph("Performance Reviews", sectionFont));

        List<PerformanceReview> reviews = performanceReviewRepository.findByEmployee(employee);

        if (reviews.isEmpty()) {
            document.add(new Paragraph("No performance reviews found"));
        } else {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addTableHeaderCell(table, "Review Date");
            addTableHeaderCell(table, "Reviewer");
            addTableHeaderCell(table, "Rating");
            addTableHeaderCell(table, "Status");

            reviews.forEach(review -> {
                addTableCell(table, review.getCreatedAt() != null ? review.getCreatedAt().toString() : "N/A");
                addTableCell(table, review.getManager() != null ?
                        review.getManager().getFirstName() + " " + review.getManager().getLastName() : "N/A");
                addTableCell(table, review.getEmployeeSelfRating() != null ? review.getEmployeeSelfRating().toString() : "N/A");
                addTableCell(table, review.getStatus() != null ? String.valueOf(review.getStatus()) : "N/A");
            });

            document.add(table);
        }

        document.add(new Paragraph(" "));
    }

    private void addGoalsSection(Document document, User employee) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        document.add(new Paragraph("Employee Goals", sectionFont));

        List<EmployeeGoal> goals = employeeGoalRepository.findByEmployee(employee);

        if (goals.isEmpty()) {
            document.add(new Paragraph("No goals found"));
        } else {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            addTableHeaderCell(table, "Goal");
            addTableHeaderCell(table, "Progress");
            addTableHeaderCell(table, "Status");

            goals.forEach(goal -> {
                addTableCell(table, goal.getGoalDescription() != null ? goal.getGoalDescription() : "N/A");
                addTableCell(table, goal.getProgressPercentage() != null ? goal.getProgressPercentage() + "%" : "0%");
                addTableCell(table, goal.getStatus() != null ? String.valueOf(goal.getStatus()) : "N/A");
            });

            document.add(table);
        }

        document.add(new Paragraph(" "));
    }

    private void addTableRow(PdfPTable table, String key, String value) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 11)));
        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    private void addTableHeaderCell(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 10))));
    }

    private boolean canAccessEmployeeReport(User loggedInUser, User targetEmployee) {
        if ("ADMIN".equals(loggedInUser.getRole().toString())) return true;
        if ("EMPLOYEE".equals(loggedInUser.getRole().toString())) return loggedInUser.getId().equals(targetEmployee.getId());
        return targetEmployee.getManager() != null && targetEmployee.getManager().getId().equals(loggedInUser.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public byte[] generateEmployeeReportBytes() {
        try {
            User currentUser = getCurrentUser();
            return generateEmployeeReport(currentUser.getId());
        } catch (DocumentException e) {
            throw new RuntimeException("PDF generation failed");
        }
    }
}