package com.dani.userregistration.services.user;

import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.model.db.User;
import com.dani.userregistration.repositories.UserRepository;
import com.dani.userregistration.services.PasswordEncoder;
import com.dani.userregistration.services.confirmationCode.ConfirmationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeService confirmationCodeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ConfirmationCode signUpUser(User newUser, Duration confirmationCodeLifeTime) {

        Optional<User> existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent() && existingUser.get().isEnabled()) {
            log.error("Error: The email address is already in use !");
            throw new UserIsAlreadyExistException("Error: The email address is already in use!");
        } else existingUser.ifPresent(user -> newUser.setId(user.getId()));

        newUser.setPassword(passwordEncoder.encrypt(newUser.getPassword()));

        userRepository.save(newUser);

        String code = UUID.randomUUID().toString();
        ConfirmationCode confirmationCode = new ConfirmationCode(
                code,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(confirmationCodeLifeTime.getSeconds()),
                newUser);
        confirmationCodeService.saveConfirmationCode(confirmationCode);

        return confirmationCode;
    }

    public int enableUser(String email) {
        return userRepository.enableUserByEmail(email);
    }

}
