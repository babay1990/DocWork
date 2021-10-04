package com.shpaginWork.docWork.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Controller
public class TestMailController {

    @Autowired
    public JavaMailSender emailSender;

    @ResponseBody
    @RequestMapping("/test/send")
    public String sendSimpleEmail() {

        // Create a Simple MailMessage.
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("shpaginjava@gmail.com");
        message.setTo("babay1990@yandex.ru");
        message.setSubject("Test Simple Email");
        message.setText("Hello, Im testing Simple Email");

        // Send Message!
        emailSender.send(message);

        return "Email Sent!";
    }

    @ResponseBody
    @RequestMapping("/test/send2")
    public String sendAttachmentEmail() throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        boolean multipart = true;

        MimeMessageHelper helper = new MimeMessageHelper(message, multipart);

        helper.setFrom("shpaginjava@gmail.com");
        helper.setTo("babay1990@yandex.ru");
        helper.setSubject("Test email with attachments");

        helper.setText("Hello, Im testing email with attachments!");

        String path1 = "C:\\Users\\user01\\Desktop\\test4.docx";


        // Attachment 1
        FileSystemResource file1 = new FileSystemResource(new File(path1));
        helper.addAttachment("file.txt", file1);


        emailSender.send(message);

        return "Email Sent!" + file1.getFilename() + "    " + file1.getPath();
    }



}
