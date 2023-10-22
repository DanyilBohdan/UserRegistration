package com.dani.userregistration;

import com.dani.userregistration.model.ConfirmationCodeRequest;
import com.dani.userregistration.model.RegistrationRequest;
import com.dani.userregistration.model.UserResponse;
import com.dani.userregistration.model.db.ConfirmationCode;
import com.dani.userregistration.model.db.User;
import com.dani.userregistration.repositories.ConfirmationCodeRepository;
import com.dani.userregistration.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ConfirmationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String APP_JSON_TYPE = "application/json";

    @BeforeEach
    public void resetRepositories() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void confirmUserWithEmptyBody_thenReturnOk400() throws Exception {
        // execute & verify
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void confirmUserWithEmptyConfigurationCode_thenReturnOk400() throws Exception {
        // setup
        ConfirmationCodeRequest wrongConfirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code("")
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(wrongConfirmationCodeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerUserAndConfirmUser_thenReturnOk200() throws Exception {

        // setup
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("testName")
                .email("test@test.com")
                .password("111111")
                .build();
        String body = objectMapper.writeValueAsString(registrationRequest);

        // execute & verify
        String responseBody = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> userOptional = userRepository.findById(response.getId());

        Assertions.assertTrue(userOptional.isPresent());

        // find confirmationCode
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUser(userOptional.get());
        Assertions.assertTrue(confirmationCode.isPresent());

        // confirm user
        ConfirmationCodeRequest confirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code(confirmationCode.get().getConfirmationCode())
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(confirmationCodeRequest)))
                .andExpect(status().isOk());

        // check that user is confirmed
        userOptional = userRepository.findById(response.getId());
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertTrue(userOptional.get().isEnabled());
    }

    @Test
    public void confirmUserWhenUserAlreadyConfirmed_thenReturnBadRequest400() throws Exception {
        // setup
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("testName")
                .email("test@test.com")
                .password("111111")
                .build();
        String body = objectMapper.writeValueAsString(registrationRequest);

        // execute & verify
        // register User
        String responseBody = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> userOptional = userRepository.findById(response.getId());
        Assertions.assertTrue(userOptional.isPresent());

        // find confirmationCode
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUser(userOptional.get());
        Assertions.assertTrue(confirmationCode.isPresent());

        // confirm user
        ConfirmationCodeRequest confirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code(confirmationCode.get().getConfirmationCode())
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(confirmationCodeRequest)))
                .andExpect(status().isOk());

        // confirm user with confirmation code again, then return Bad Request
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void confirmUserWithWrongConfigurationCode_thenReturnBadRequest400() throws Exception {
        // setup
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("testName")
                .email("test@test.com")
                .password("111111")
                .build();
        String body = objectMapper.writeValueAsString(registrationRequest);

        // execute & verify
        // register User
        String responseBody = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> userOptional = userRepository.findById(response.getId());
        Assertions.assertTrue(userOptional.isPresent());

        // find confirmationCode
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUser(userOptional.get());
        Assertions.assertTrue(confirmationCode.isPresent());

        // confirm user with wrong code
        ConfirmationCodeRequest wrongConfirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code(confirmationCode.get().getConfirmationCode()+"1")
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(wrongConfirmationCodeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAndConfirmUserWhenCodeIsExpired_thenReturnBadRequest400() throws Exception {

        // setup
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("testName")
                .email("test@test.com")
                .password("111111")
                .build();
        String body = objectMapper.writeValueAsString(registrationRequest);

        // execute & verify
        String responseBody = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> userOptional = userRepository.findById(response.getId());

        Assertions.assertTrue(userOptional.isPresent());

        // find confirmationCode
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUser(userOptional.get());
        Assertions.assertTrue(confirmationCode.isPresent());

        // confirm user
        ConfirmationCodeRequest confirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code(confirmationCode.get().getConfirmationCode())
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(confirmationCodeRequest)))
                .andExpect(status().isOk());

        // check that user is confirmed
        userOptional = userRepository.findById(response.getId());
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertTrue(userOptional.get().isEnabled());
    }
}
