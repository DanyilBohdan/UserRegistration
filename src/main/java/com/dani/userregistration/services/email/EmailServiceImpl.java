package com.dani.userregistration.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@Profile("!mock")
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendEmail(String confirmationCode, String email, Long confirmationCodeLifeTime) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("hello@amigoscode.com");
            mailMessage.setTo(email);
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setText(String.format(MASSAGE_TEXT, confirmationCode, confirmationCodeLifeTime));
            javaMailSender.send(mailMessage);

            log.debug("Confirmation Code: " + confirmationCode);
        } catch (MailException e) {
            log.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email!");
        }
    }
}
