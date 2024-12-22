package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Ticket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class MailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendMailMessage(String toEmail,String body,String subject) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("phong2552001@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);
        javaMailSender.send(mimeMessage);
    }
    public void sendMailWithAttachment(String toEmail,String body,String subject,String attachment) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("phong2552001@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);
        String pdfpath = FileSystems.getDefault().getPath(new String("./")).toAbsolutePath().getParent() + "\\src\\main\\resources\\static\\PdfOrders\\" + attachment + ".pdf";
        FileSystemResource fileSystemResource = new FileSystemResource(new File(pdfpath));
        if(fileSystemResource!=null){
            System.out.println(fileSystemResource.getFile().toPath());
            mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()),fileSystemResource);
            javaMailSender.send(mimeMessage);
            System.out.println("Send mail successfully!");
            Files.delete(Paths.get(pdfpath));
        }
        else{
            System.out.println("File is not found!");
        }
    }
    public void sendMailWithAttachment_Ticket(String toEmail, String body, String subject, ArrayList<Ticket> tickets) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("phong2552001@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);
        for(Ticket ticket : tickets){
            String pdfpath = System.getProperty("user.dir") + "\\" + ticket.getCustomer().getFullname().trim() + "_Ticket.pdf";
            FileSystemResource fileSystemResource = new FileSystemResource(new File(pdfpath));
            if(fileSystemResource!=null)
                mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()),fileSystemResource);
            else
                System.out.println("File is not found!");
        }
        javaMailSender.send(mimeMessage);
        for(Ticket tic:tickets){
            String pdfpath = System.getProperty("user.dir") + "\\" + tic.getCustomer().getFullname().trim() + "_Ticket.pdf";
            Files.deleteIfExists(Paths.get(pdfpath));
        }
        System.out.println("Send mail successfully!");
    }
}
