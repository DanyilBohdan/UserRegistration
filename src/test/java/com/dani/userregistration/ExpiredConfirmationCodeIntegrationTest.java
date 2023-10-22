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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"confirmation.code.life.time=PT5S"})
public class ExpiredConfirmationCodeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String APP_JSON_TYPE = "application/json";

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

        // wait 6 seconds
        Thread.sleep(6000);

        // confirm user
        ConfirmationCodeRequest confirmationCodeRequest = ConfirmationCodeRequest.builder()
                .code(confirmationCode.get().getConfirmationCode())
                .build();
        mockMvc.perform(post("/api/v1/registration/confirm")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(confirmationCodeRequest)))
                .andExpect(status().isConflict());
    }
}
