package com.reservation.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.reservation.entity.Reservation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReservationEmail(String to, Reservation reservation) {

        try {
            byte[] pdf = generatePdf(reservation);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Reservation Report");
            helper.setText("""
                        <h2>Reservation Confirmed</h2>
                        <p>Your booking has been successfully created.</p>
                        <p>Please find your receipt attached.</p>
                    """, true);
            helper.setFrom("manastare27@gmail.com");
            helper.addAttachment(
                    "reservation_" + reservation.getId() + ".pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email ", e);
        }
    }

    public byte[] generatePdf(Reservation reservation) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Reservation Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        LineSeparator line = new LineSeparator();
        document.add(line);

        // Add spacing
        document.add(new Paragraph(" "));

        // Section title
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Paragraph detailsTitle = new Paragraph("Reservation Details", sectionFont);
        detailsTitle.setSpacingAfter(10);
        document.add(detailsTitle);

        // Table (2 columns: label + value)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Helper fonts
        Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 12);

        // Helper method style (manual)
        addRow(table, "Reservation ID", String.valueOf(reservation.getId()), labelFont, valueFont);
        //addRow(table, "Guest Name", reservation.getGuestName(), labelFont, valueFont);
        //addRow(table, "Email", reservation.getGuestEmail(), labelFont, valueFont);
        addRow(table, "Bungalow ID", String.valueOf(reservation.getBungalowId()), labelFont, valueFont);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        addRow(table, "Arrival Date", reservation.getArrivalDate().format(formatter), labelFont, valueFont);
        addRow(table, "Departure Date", reservation.getDepartureDate().format(formatter), labelFont, valueFont);

        addRow(table, "Status", reservation.getReservationStatus().toString(), labelFont, valueFont);

        Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        addRow(table, "Total Amount", "₹ " + reservation.getTotalAmount(), totalFont, totalFont);

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Paragraph(label, labelFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph(value, valueFont));

        cell1.setPadding(8);
        cell2.setPadding(8);

        cell1.setBorderWidth(0.5f);
        cell2.setBorderWidth(0.5f);

        table.addCell(cell1);
        table.addCell(cell2);
    }
}
