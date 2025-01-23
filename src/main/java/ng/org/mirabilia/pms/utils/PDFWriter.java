package ng.org.mirabilia.pms.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import ng.org.mirabilia.pms.domain.entities.Installment;
import ng.org.mirabilia.pms.domain.entities.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class PDFWriter {
        public byte[] generateInvoicePdf(Invoice invoice) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();

            try {
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                document.open();

                InputStream imageStream = getClass().getResourceAsStream("/image/OctoberClearance.docx");
                if (imageStream != null) {
                    byte[] imageBytes = imageStream.readAllBytes();
                    Image letterhead = Image.getInstance(imageBytes);
                    letterhead.setAbsolutePosition(0, PageSize.A4.getHeight() - 150); // Position at the top
                    letterhead.scaleToFit(PageSize.A4.getWidth(), 150); // Scale to fit width
                    document.add(letterhead);
                } else {
                    System.out.println("Image not found in resources");
                }


                // Add content below the letterhead
                document.add(new Paragraph("\n\n\n\nClient Information:")); // Spacing to move below the letterhead
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Name : " + invoice.getUserNameOrUserCode().getFirstName() + " " + invoice.getUserNameOrUserCode().getLastName()));

                document.add(new Paragraph(" "));
                document.add(new Paragraph("Invoice Details:"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceCode()));
                document.add(new Paragraph("Invoice Date: " + invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
                document.add(new Paragraph("Invoice Due Date for First Payment: " + invoice.getInstallmentalPaymentList().get(0).getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));

                // Add a table
                PdfPTable table = new PdfPTable(3); // 3 columns: Due Date, Amount, Payment Status
                table.addCell("Due Date");
                table.addCell("Amount");
                table.addCell("Payment Status");

                for (Installment installment : invoice.getInstallmentalPaymentList()) {
                    table.addCell(installment.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    table.addCell("₦" + new DecimalFormat("#,###").format(installment.getPrice()));
                    table.addCell(String.valueOf(installment.getInvoiceStatus()));
                }
                document.add(new Paragraph(" "));
                document.add(table);

                // Payment and footer details
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Payment Information:"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Payment should be done by transfer and proof of payment uploaded to the system."));
                document.add(new Paragraph("Account Number: 0655602501"));
                document.add(new Paragraph("Bank: Guaranty Trust Bank (GTBANK)"));
                document.add(new Paragraph("Account Name: LydiArit Properties"));

                document.add(new Paragraph(" "));
                document.add(new Paragraph("Total Amount: ₦" + new DecimalFormat("#,###").format(invoice.getPropertyCode().getPrice())));
                document.add(new Paragraph("Payment Terms: " + invoice.getPropertyCode().getInstallmentalPayments().toString().replace("_", " ").toLowerCase()));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Thank you for your business!"));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
            }

            return outputStream.toByteArray();
        }
    }