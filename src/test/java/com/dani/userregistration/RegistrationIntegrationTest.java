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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RegistrationIntegrationTest {

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
    public void registerUserWithoutRequestBody_thenBadRequest() throws Exception {
        // execute & verify
        mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerUserWithEmptyRequestBody_thenBadRequest() throws Exception {
        // execute & verify
        mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Rule for Password
     */
    @Test
    public void registerUserWhenPasswordIsInvalid_thenBadRequest() throws Exception {

        // setup
        List<String> requests = List.of(
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.com")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.com")
                                .password("")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.com")
                                .password("11")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.com")
                                .password("11111111111111111111111111")
                                .build())
        );

        // execute & verify
        requests.forEach(body -> {
            try {
                mockMvc.perform(post("/api/v1/registration")
                                .contentType(APP_JSON_TYPE)
                                .content(body))
                        .andExpect(status().isBadRequest());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Rule for Name
     * The name is required. MIN = 2, MAX = 32
     */
    @Test
    public void registerUserWhenNameIsInvalid_thenBadRequest() throws Exception {

        // setup
        List<String> requests = List.of(
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .email("test@test.com")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("")
                                .email("test@test.com")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("t")
                                .email("test@test.com")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testNametestNametestNametestNametestNametestNametestNametestNametestName")
                                .email("test@test.com")
                                .password("111111")
                                .build())
        );

        // execute & verify
        requests.forEach(body -> {
            try {
                mockMvc.perform(post("/api/v1/registration")
                                .contentType(APP_JSON_TYPE)
                                .content(body))
                        .andExpect(status().isBadRequest());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Rule for email
     * The name is required. Example pattern: example@example.example
     */
    @Test
    public void registerUserWhenEmailIsInvalid_thenBadRequest() throws Exception {

        // setup
        List<String> requests = List.of(
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.c")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("testtest.com")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("test@test.")
                                .password("111111")
                                .build()),
                objectMapper.writeValueAsString(
                        RegistrationRequest.builder()
                                .name("testName")
                                .email("@test.com")
                                .password("111111")
                                .build())
        );

        // execute & verify
        requests.forEach(body -> {
            try {
                mockMvc.perform(post("/api/v1/registration")
                                .contentType(APP_JSON_TYPE)
                                .content(body))
                        .andExpect(status().isBadRequest());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void registerUserWhenEmailIsValid_thenReturnOk200() throws Exception {

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
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.name", is(registrationRequest.getName())))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.email", is(registrationRequest.getEmail())))
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> user = userRepository.findById(response.getId());

        Assertions.assertTrue(user.isPresent());
    }

    @Test
    public void registerUserWhenEmailIsSameButIsNotActivated_thenReturnOk() throws Exception {

        // setup
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("testName")
                .email("test@test.com")
                .password("111111")
                .build();

        // execute & verify
        // first request
        String responseBody1 = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.name", is(registrationRequest.getName())))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.email", is(registrationRequest.getEmail())))
                .andReturn().getResponse().getContentAsString();

        UserResponse firstResponse = objectMapper.readValue(responseBody1, UserResponse.class);
        Optional<User> user = userRepository.findById(firstResponse.getId());

        Assertions.assertTrue(user.isPresent());

        // second request with other name
        registrationRequest.setName("otherName");
        String responseBody2 = mockMvc.perform(post("/api/v1/registration")
                        .contentType(APP_JSON_TYPE)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.name", is(registrationRequest.getName())))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.email", is(registrationRequest.getEmail())))
                .andReturn().getResponse().getContentAsString();

        UserResponse secondResponse = objectMapper.readValue(responseBody2, UserResponse.class);
        user = userRepository.findById(secondResponse.getId());

        Assertions.assertTrue(user.isPresent());

        // check email and id both users
        Assertions.assertEquals(firstResponse.getId(), secondResponse.getId());
        Assertions.assertEquals(firstResponse.getEmail(), secondResponse.getEmail());
    }

    @Test
    public void registerUserWhenConfirmationCodeIsSaved_thenReturnOk200() throws Exception {
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
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.name", is(registrationRequest.getName())))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.email", is(registrationRequest.getEmail())))
                .andReturn().getResponse().getContentAsString();

        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        Optional<User> user = userRepository.findById(response.getId());

        Assertions.assertTrue(user.isPresent());

        // find confirmationCode
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUser(user.get());
        Assertions.assertTrue(confirmationCode.isPresent());
    }

}
