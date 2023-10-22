package com.dani.userregistration.repositories;

import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    Optional<ConfirmationCode> findByConfirmationCode(String confirmationCode);

    Optional<ConfirmationCode> findByUser(User user);
}
