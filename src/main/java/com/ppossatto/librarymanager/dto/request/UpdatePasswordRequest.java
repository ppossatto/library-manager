package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePasswordRequest(
   @Size(min = 8, message = "The old password have at least 8 characters")
   String oldPassword,
   @NotBlank(message = "The new password field must be provided")
   @Size(min = 8, message = "The new password must have at least 8 characters")
   String newPassword
) {
}
