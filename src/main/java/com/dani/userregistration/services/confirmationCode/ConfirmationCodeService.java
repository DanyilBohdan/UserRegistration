package com.dani.userregistration.services.confirmationCode;

import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.repositories.ConfirmationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmationCodeService {

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    public void saveConfirmationCode(ConfirmationCode confirmationCode) {
        Optional<ConfirmationCode> existingConfirmationCode = confirmationCodeRepository.findByUser(confirmationCode.getUser());
        existingConfirmationCode.ifPresent(code -> confirmationCode.setId(code.getId()));
        confirmationCodeRepository.save(confirmationCode);
    }

    public ConfirmationCode getCode(String code) {
        return confirmationCodeRepository.findByConfirmationCode(code)
                .orElseThrow(() -> new InvalidConfirmationCodeException("The confirmation code is invalid."));
    }

}
