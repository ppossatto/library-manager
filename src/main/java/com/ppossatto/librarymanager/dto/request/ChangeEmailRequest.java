package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangeEmailRequest(
   @NotBlank(message = "The new email field must be provided")
   @Email(message = "The new email does not follow the correct format")
   String newEmail,
   @NotBlank(message = "The field password must be provided")
   @Size(min = 8, message = "The password must contain at least 8 characters")
   String password
) {
}
