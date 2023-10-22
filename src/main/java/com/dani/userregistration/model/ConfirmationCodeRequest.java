package com.dani.userregistration.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCodeRequest {

    @NotBlank(message = "The confirmationCode is required.")
    private String code;
}
