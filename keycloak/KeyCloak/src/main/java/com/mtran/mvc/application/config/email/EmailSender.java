package com.mtran.mvc.application.config.email;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject,String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tuanminhlienbao@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Mail sent successfully");
    }
}
