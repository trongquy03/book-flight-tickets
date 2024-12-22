package com.kttt.webbanve.services;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.text.BaseColor;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.services.MailSenderService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;


@Service
public class GeneratePdfService {
    public void htmlToPdf(String processedHtml,String order) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        DefaultFontProvider defaultFont = new DefaultFontProvider(false,true,false);
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setFontProvider(defaultFont);
        HtmlConverter.convertToPdf(processedHtml,pdfWriter,converterProperties);
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\PdfOrders\\" + order + ".pdf";
        FileOutputStream fout = new FileOutputStream(path);
        byteArrayOutputStream.writeTo(fout);
        byteArrayOutputStream.close();
        byteArrayOutputStream.flush();
        fout.close();
    }
    public void addImgToPDF(String qrcode) throws Exception{
        String pathImg = FileSystems.getDefault().getPath(new String("./")).toAbsolutePath() + "\\src\\main\\resources\\static\\qrcodes\\" + Base64.getEncoder().encodeToString(qrcode.getBytes()) + ".png";
        String pathPdf = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\PdfOrders\\" + qrcode + ".pdf";
        File filePdf = new File(pathPdf);
        PDDocument doc = new PDDocument().load(filePdf);
        PDPage page = new PDPage();
        doc.addPage(page);
        PDImageXObject image = PDImageXObject.createFromFile(pathImg,doc);
        PDPageContentStream contents  = new PDPageContentStream(doc,page);
        contents.drawImage(image,150f,300f,300,300);
        contents.close();
        doc.save(filePdf);
        doc.close();
    }
    public void exportTicket(ArrayList<Ticket> tickets) throws FileNotFoundException, MalformedURLException {
        for (Ticket ticket : tickets) {
            String pathFile = System.getProperty("user.dir") + "\\" + ticket.getCustomer().getFullname().trim() + "_Ticket.pdf";
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pathFile));
            pdfDocument.addNewPage(PageSize.A4);
            Document document = new Document(pdfDocument);
            float[] columWidth = {200f, 200f, 200f};
            Table table = new Table(columWidth);
            // header cua ve
            Cell cellH1 = new Cell();
            Cell cellH2 = new Cell();
            Cell cellH3 = new Cell();
            cellH1.add(new Paragraph(new String(ticket.getSeat().getPlane().getAirlineCompany().getAirline_name()).toUpperCase()).setBold().setFontColor(DeviceGray.WHITE).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(20f));
            cellH2.add(new Paragraph(new String(ticket.getSeat().getSeatCategory().getCategoryName()).toUpperCase()).setBold().setFontColor(DeviceGray.WHITE).setTextAlignment(TextAlignment.RIGHT));
            cellH1.setFontSize(15);
            cellH2.setFontSize(15);
            cellH1.setBackgroundColor(DeviceGray.BLACK);
            cellH2.setBackgroundColor(DeviceGray.BLACK);
            cellH3.setBackgroundColor(DeviceGray.BLACK);
            table.addCell(cellH1.setBorder(Border.NO_BORDER));
            table.addCell(cellH2.setBorder(Border.NO_BORDER));
            table.addCell(cellH3.setBorder(Border.NO_BORDER));
            // content cua ve
            ImageData img = ImageDataFactory.create(System.getProperty("user.dir") + "\\src\\main\\resources\\static\\barcodes\\" + ticket.getBarCode() + ".png");
            Image image = new Image(img);
            for (int i = 0; i < 4; i++) {
                Cell cell1 = new Cell();
                Cell cell2 = new Cell();
                Cell cell3 = new Cell();
                cell1.setBorder(Border.NO_BORDER);
                cell2.setBorder(Border.NO_BORDER);
                cell3.setBorder(Border.NO_BORDER);
                // Noi dung tung cot
                if(i == 0) {
                    cell2.add(image.scaleAbsolute(200f, 20f));
                    cell1.add(new Paragraph(ticket.getSeat().getPlane().getAirlineCompany().getAirline_name().toUpperCase()));
                    cell3.add(new Paragraph(ticket.getBarCode().toUpperCase()));
                }
                if(i == 1){
                    cell1.add(new Paragraph(ticket.getCustomer().getFullname().toUpperCase()));
                    cell2.add(new Paragraph("Gate: " + new Random().nextInt(1,10) + "\t\t\t\t" + "Date: " + ticket.getFlight().getFlightTime()));
                    cell3.add(new Paragraph("Fr:" + ticket.getFlight().getFlightTime() + "\t\t\t\t" + "To:" + ticket.getFlight().getDepartureTime()));
                }
                if(i == 2){
                    cell1.add(new Paragraph("From: " + ticket.getFlight().getFlightTime()));
                    cell2.add(new Paragraph("Seat: " + ticket.getSeat().getPosition() + "\t\t\t\t" + "19:30"));
                    cell3.add(new Paragraph(ticket.getCustomer().getFullname().toUpperCase()));
                }
                if(i == 3){
                    cell1.add(new Paragraph(ticket.getBarCode()));
                    cell3.add(new Paragraph(ticket.getFlight().getFlightTime() + "\t\t | \t\t" + ticket.getSeat().getPosition()));
                }
                cell3.setBorderLeft(new DottedBorder(0.1f));
                table.addCell(cell1);
                table.addCell(cell2.setTextAlignment(TextAlignment.CENTER));
                table.addCell(cell3);
            }
            //footer
            table.addCell(new Cell().setBackgroundColor(DeviceGray.BLACK).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setBackgroundColor(DeviceGray.BLACK).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setBackgroundColor(DeviceGray.BLACK).setBorder(Border.NO_BORDER));
            table.setMarginTop(20f);
            document.add(table.setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(0.1f)));
            document.close();
        }
    }
}
