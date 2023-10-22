package com.dani.userregistration.controllers;

import com.dani.userregistration.model.ConfirmationCodeRequest;
import com.dani.userregistration.model.UserResponse;
import com.dani.userregistration.model.RegistrationRequest;
import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller
 */
@RestController
@RequestMapping("api/v1/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    /**
     * /api/v1/registration - registers User and sends confirmation code to email
     *
     * @param registrationRequest - request body of registration user
     *
     * @return
     *          200 - UserResponse object with data about user.
     *          400 - if user has already is used (user has same email and confirmed)
     *                if any variable of body is invalid
     *          500 - error on server side
     */
    @PostMapping()
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {

        ConfirmationCode confirmationCode = registrationService.register(registrationRequest);
        return ResponseEntity.ok(
                UserResponse.builder()
                .id(confirmationCode.getUser().getId())
                .name(confirmationCode.getUser().getName())
                .email(confirmationCode.getUser().getEmail())
                .isActivated(confirmationCode.getUser().isEnabled())
                .build());
    }

    /**
     * /api/v1/registration/confirm - Confirms User and makes user as active
     *
     * @param confirmationCodeRequest - request body with confirmation code
     * @return 200 - if user was activated.
     *         400 - if user has already is activated
     *               if confirmation code is invalid
     *         409 - when confirmation code is expired
     */
    @PostMapping(path = "confirm")
    public ResponseEntity<?> confirm(@Valid @RequestBody ConfirmationCodeRequest confirmationCodeRequest) {
        registrationService.confirmEmail(confirmationCodeRequest.getCode());
        return ResponseEntity.ok().build();
    }
}
