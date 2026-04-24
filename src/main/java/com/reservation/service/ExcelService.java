package com.reservation.service;

import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import com.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ReservationRepository reservationRepository;

    public ByteArrayInputStream exportToExcel() throws IOException {

        List<Reservation> data = reservationRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("arrival_date");
        header.createCell(2).setCellValue("bungalow_id");
        header.createCell(3).setCellValue("created_at");
        header.createCell(4).setCellValue("departure_date");
        header.createCell(5).setCellValue("guest_name");
        header.createCell(6).setCellValue("guest_email");
        header.createCell(7).setCellValue("reservation_status");
        header.createCell(8).setCellValue("total_amount");

        int rowIdx = 1;

        for (Reservation reservation : data){
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(reservation.getId());
            row.createCell(1).setCellValue(reservation.getArrivalDate().toString());
            row.createCell(2).setCellValue(reservation.getBungalowId());
            row.createCell(3).setCellValue(reservation.getCreatedAt().toString());
            row.createCell(4).setCellValue(reservation.getDepartureDate().toString());
            //row.createCell(5).setCellValue(reservation.getGuestName());
            //row.createCell(6).setCellValue(reservation.getGuestEmail());
            row.createCell(7).setCellValue(reservation.getReservationStatus().toString());
            row.createCell(8).setCellValue(reservation.getTotalAmount());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public void saveExcelData(MultipartFile file) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Reservation> list = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header

            Reservation entity = new Reservation();

            entity.setArrivalDate(
                    LocalDate.parse(row.getCell(1).getStringCellValue())
            );

            entity.setBungalowId((int) row.getCell(2).getNumericCellValue());

            entity.setCreatedAt(
                    LocalDateTime.parse(row.getCell(3).getStringCellValue())
            );

            entity.setDepartureDate(
                    LocalDate.parse(row.getCell(4).getStringCellValue())
            );

            //entity.setGuestName(row.getCell(5).getStringCellValue());

            //entity.setGuestEmail(row.getCell(6).getStringCellValue());

            entity.setReservationStatus(
                    ReservationStatus.valueOf(row.getCell(7).getStringCellValue())
            );

            entity.setTotalAmount(row.getCell(8).getNumericCellValue());

            list.add(entity);
        }

        reservationRepository.saveAll(list);
        workbook.close();
    }
}
