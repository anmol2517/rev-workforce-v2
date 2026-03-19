package com.revworkforce.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.revworkforce.entity.User;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@AllArgsConstructor
public class QRCodeService {

    private final UserRepository userRepository;
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    public String generateEmployeeQRCode(Long employeeId) throws WriterException, java.io.IOException {
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        return generateQRCodeAsBase64(generateQRData(employee));
    }

    public byte[] generateEmployeeQRCodeImage(Long employeeId) throws WriterException, java.io.IOException {
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        return generateQRCodeAsImage(generateQRData(employee));
    }

    private String generateQRData(User employee) {
        return String.format("EID:%s|EMAIL:%s|NAME:%s|DEPT:%s|DESIG:%s",
                employee.getId(), employee.getEmail(), employee.getFirstName() + " " + employee.getLastName(),
                employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A",
                employee.getDesignation() != null ? employee.getDesignation().getName() : "N/A");
    }

    private String generateQRCodeAsBase64(String data) throws WriterException, java.io.IOException {
        return Base64.getEncoder().encodeToString(generateQRCodeAsImage(data));
    }

    private byte[] generateQRCodeAsImage(String data) throws WriterException, java.io.IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        com.google.zxing.client.j2se.MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public String generateQRCodeForCurrentUser() {
        try {
            Long userId = userRepository.findAll().get(0).getId();
            return generateEmployeeQRCode(userId);
        } catch (Exception e) {
            throw new RuntimeException("QR Generation failed");
        }
    }

    public ResponseEntity<byte[]> generateQRCodeImageForCurrentUser() {
        try {
            Long userId = userRepository.findAll().get(0).getId();
            byte[] image = generateEmployeeQRCodeImage(userId);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
