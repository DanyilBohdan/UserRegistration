package com.dani.userregistration.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class PasswordEncoder {

    private static final String SECRET_WORD = "Secret phrase of User manager";

    private final SecretKey secretKey;

    public PasswordEncoder() {
        secretKey = generateSecretKey();
    }

    private SecretKey generateSecretKey() {
        byte[] keyBytes = PasswordEncoder.SECRET_WORD.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
    }

    public String encrypt(String code) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(code.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception ex) {
            log.error("Error with password encryption");
            throw new EncodingException("Error with password encryption.");
        }
    }

    public String decrypt(String encryptedCode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedCode);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Error with password decryption");
            throw new EncodingException("Error with password decryption.");
        }
    }
}
