package com.dani.userregistration.services;

import com.dani.userregistration.model.RegistrationRequest;
import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.model.db.User;
import com.dani.userregistration.services.confirmationCode.ConfirmationCodeService;
import com.dani.userregistration.services.confirmationCode.ExpiredConfirmationCodeException;
import com.dani.userregistration.services.confirmationCode.InvalidConfirmationCodeException;
import com.dani.userregistration.services.email.EmailService;
import com.dani.userregistration.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class RegistrationService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationCodeService confirmationCodeService;

    @Value("${confirmation.code.life.time}")
    private String confirmationCodeLifeTime;

    public ConfirmationCode register(RegistrationRequest registrationRequest) {

        Duration confirmationCodeLifeTimeDuration = Duration.parse(confirmationCodeLifeTime);

        ConfirmationCode confirmationCode = userService.signUpUser(
                User.builder()
                        .name(registrationRequest.getName())
                        .email(registrationRequest.getEmail())
                        .password(registrationRequest.getPassword())
                        .isEnabled(false)
                        .build(),
                confirmationCodeLifeTimeDuration
        );

        emailService.sendEmail(confirmationCode.getConfirmationCode(), registrationRequest.getEmail(), confirmationCodeLifeTimeDuration.toMinutes());

        return confirmationCode;
    }

    @Transactional
    public void confirmEmail(String code) {
        ConfirmationCode confirmationCode = confirmationCodeService.getCode(code);

        if (confirmationCode.getUser().isEnabled()) {
            log.error("Error: User has already activated.");
            throw new InvalidConfirmationCodeException("User has already activated.");
        }
        if(confirmationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Error: Confirmation code is expired.");
            throw new ExpiredConfirmationCodeException("Confirmation code is expired! Please, send data to /api/v1/registration again for send confirmation code again.");
        }

        userService.enableUser(confirmationCode.getUser().getEmail());
    }
}
