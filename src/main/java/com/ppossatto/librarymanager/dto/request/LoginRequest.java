package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest(
   @NotBlank(message = "The email field must be provided")
   @Email(message = "The given email does not have the correct format")
   String email,
   @NotBlank(message = "The password field must be provided")
   @Size(min = 8, message = "The password must have at least 8 characters")
   String password
) {
}
