package com.reservation.controller;

import com.reservation.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportReservations() {
        try {
            ByteArrayInputStream fileStream = excelService.exportToExcel();
            long fileSize = fileStream.available();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations.xlsx")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importReservations(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("File is required and cannot be empty");
            }

            excelService.saveExcelData(file);

            return ResponseEntity.ok("Reservations imported successfully from file: " + file.getOriginalFilename());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid file: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please contact support.");
        }
    }
}
