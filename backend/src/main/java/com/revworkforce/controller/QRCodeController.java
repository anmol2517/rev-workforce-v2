package com.revworkforce.controller;

import com.revworkforce.service.QRCodeService;
import com.revworkforce.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qrcode")
@AllArgsConstructor
@Tag(name = "QR Code", description = "Employee QR code generation for mobile access")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee QR code", description = "Generate QR code for employee details (Base64 encoded)")
    public ResponseEntity<ApiResponse> getEmployeeQRCode(@PathVariable Long employeeId) {
        try {
            String qrCodeBase64 = qrCodeService.generateEmployeeQRCode(employeeId);

            Map<String, String> data = new HashMap<>();
            data.put("qrCode", qrCodeBase64);
            data.put("employeeId", employeeId.toString());
            data.put("format", "base64");

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("QR Code generated in Base64 format")
                    .data(data)
                    .status("success")
                    .timestamp(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error generating QR code: " + e.getMessage())
                    .status("error")
                    .build());
        }
    }

    @GetMapping("/employee/{employeeId}/image")
    @Operation(summary = "Download employee QR code image", description = "Download QR code as PNG image file")
    public ResponseEntity<byte[]> downloadEmployeeQRCodeImage(@PathVariable Long employeeId) {
        try {
            byte[] imageBytes = qrCodeService.generateEmployeeQRCodeImage(employeeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "employee_qrcode_" + employeeId + ".png");
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/{employeeId}/preview")
    @Operation(summary = "Preview employee QR code", description = "Preview QR code as inline PNG image")
    public ResponseEntity<byte[]> previewEmployeeQRCode(@PathVariable Long employeeId) {
        try {
            byte[] imageBytes = qrCodeService.generateEmployeeQRCodeImage(employeeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}