package com.dani.userregistration.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@Profile("mock")
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(String confirmationCode, String email, Long confirmationCodeLifeTime) {
        log.info(String.format("Send to email %s with message: " + MASSAGE_TEXT, email, confirmationCode, confirmationCodeLifeTime));
    }
}
