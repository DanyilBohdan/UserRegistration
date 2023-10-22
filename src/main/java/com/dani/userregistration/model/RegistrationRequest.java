package com.dani.userregistration.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationRequest {

    @NotBlank(message = "The name is required. MIN = 2, MAX = 32")
    @Size(min = 2, max = 32, message = "The name is invalid. MIN = 2, MAX = 32")
    private String name;

    @NotBlank(message = "The email is required")
    @Pattern(regexp = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}", message = "The email is invalid. Example pattern: example@example.example")
    private String email;

    @NotBlank(message = "The password is required. MIN = 4, MAX = 24")
    @Size(min = 4, max = 24, message = "The password is invalid. MIN = 2, MAX = 24")
    private String password;
}
