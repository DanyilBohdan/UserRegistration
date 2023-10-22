package com.dani.userregistration.services.email;

import java.time.Duration;

public interface EmailService {

    String MASSAGE_TEXT = "Your confirmation code for activate user: %s. Link will expire in %d minutes.";

    void sendEmail(String confirmationCode, String email, Long confirmationCodeLifeTime);
}
