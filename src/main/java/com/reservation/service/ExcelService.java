package com.reservation.service;

import com.reservation.entity.Guest;
import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ReservationRepository reservationRepository;

    // Column indices for better maintainability
    private static final int COL_ID = 0;
    private static final int COL_ARRIVAL_DATE = 1;
    private static final int COL_BUNGALOW_ID = 2;
    private static final int COL_CREATED_AT = 3;
    private static final int COL_DEPARTURE_DATE = 4;
    private static final int COL_GUEST_NAME = 5;
    private static final int COL_GUEST_EMAIL = 6;
    private static final int COL_RESERVATION_STATUS = 7;
    private static final int COL_TOTAL_AMOUNT = 8;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ByteArrayInputStream exportToExcel() {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()){

            List<Reservation> reservations = reservationRepository.findAll();
            Sheet sheet = workbook.createSheet("Reservations");

            // Create header
            Row header = sheet.createRow(0);
            header.createCell(COL_ID).setCellValue("id");
            header.createCell(COL_ARRIVAL_DATE).setCellValue("arrival_date");
            header.createCell(COL_BUNGALOW_ID).setCellValue("bungalow_id");
            header.createCell(COL_CREATED_AT).setCellValue("created_at");
            header.createCell(COL_DEPARTURE_DATE).setCellValue("departure_date");
            header.createCell(COL_GUEST_NAME).setCellValue("guest_name");
            header.createCell(COL_GUEST_EMAIL).setCellValue("guest_email");
            header.createCell(COL_RESERVATION_STATUS).setCellValue("reservation_status");
            header.createCell(COL_TOTAL_AMOUNT).setCellValue("total_amount");

            // Create data rows
            int rowIdx = 1;
            for (Reservation reservation : reservations) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(COL_ID).setCellValue(reservation.getId());
                row.createCell(COL_ARRIVAL_DATE).setCellValue(reservation.getArrivalDate().toString());
                row.createCell(COL_BUNGALOW_ID).setCellValue(reservation.getBungalowId());
                row.createCell(COL_CREATED_AT).setCellValue(reservation.getCreatedAt().toString());
                row.createCell(COL_DEPARTURE_DATE).setCellValue(reservation.getDepartureDate().toString());
                row.createCell(COL_GUEST_NAME).setCellValue(reservation.getGuest().getName());
                row.createCell(COL_GUEST_EMAIL).setCellValue(reservation.getGuest().getEmail());
                row.createCell(COL_RESERVATION_STATUS).setCellValue(reservation.getReservationStatus().toString());
                row.createCell(COL_TOTAL_AMOUNT).setCellValue(reservation.getTotalAmount());
            }
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export reservations to Excel", e);
        }
    }

    @Transactional
    public void saveExcelData(MultipartFile file) {
        validateExcelFile(file);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel file must contain at least one sheet");
            }

            List<Reservation> reservations = new ArrayList<>();
            int processedRows = 0;
            int errorRows = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                try {
                    Reservation reservation = parseReservationFromRow(row);
                    reservations.add(reservation);
                    processedRows++;
                } catch (Exception e) {
                    errorRows++;
                }
            }

            if (!reservations.isEmpty()) {
                reservationRepository.saveAll(reservations);
            } else {
                throw new IllegalArgumentException("No valid reservations found in Excel file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }
    }

    // Private helper methods
    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required and cannot be empty");
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename) || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("File must be a valid .xlsx file");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }
    }

    private Reservation parseReservationFromRow(Row row) {
        Reservation reservation = new Reservation();

        // Parse dates with error handling
        reservation.setArrivalDate(parseDate(getCellStringValue(row, COL_ARRIVAL_DATE), "arrival date"));
        reservation.setDepartureDate(parseDate(getCellStringValue(row, COL_DEPARTURE_DATE), "departure date"));
        reservation.setCreatedAt(parseDateTime(getCellStringValue(row, COL_CREATED_AT), "created at"));

        // Parse numeric values
        reservation.setBungalowId((int) getCellNumericValue(row, COL_BUNGALOW_ID, "bungalow ID"));
        reservation.setTotalAmount(getCellNumericValue(row, COL_TOTAL_AMOUNT, "total amount"));

        // Parse status
        String statusStr = getCellStringValue(row, COL_RESERVATION_STATUS);
        try {
            reservation.setReservationStatus(ReservationStatus.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid reservation status: " + statusStr);
        }

        // Create and set guest
        Guest guest = new Guest();
        guest.setName(getCellStringValue(row, COL_GUEST_NAME));
        guest.setEmail(getCellStringValue(row, COL_GUEST_EMAIL).toLowerCase().trim());
        reservation.setGuest(guest);

        return reservation;
    }

    private String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            throw new IllegalArgumentException("Cell " + cellIndex + " is empty");
        }

        String value = cell.getStringCellValue();
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Cell " + cellIndex + " is empty or contains only whitespace");
        }

        return value.trim();
    }

    private double getCellNumericValue(Row row, int cellIndex, String fieldName) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid numeric value for " + fieldName);
        }
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for " + fieldName + ": " + dateStr);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr, String fieldName) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format for " + fieldName + ": " + dateTimeStr);
        }
    }
}
