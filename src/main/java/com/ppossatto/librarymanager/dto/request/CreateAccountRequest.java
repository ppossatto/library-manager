package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateAccountRequest(
   @NotBlank(message = "The field title must be provided")
   @Size(max = 150, message = "The title cannot have more than 150 characters")
   String name,
   @NotBlank(message = "The field email must be provided")
   @Email(message = "The given email does not have the correct format")
   @Size(max = 150, message = "The email cannot have more than 150 characters")
   String email,
   @Size(max = 20, message = "The phone number cannot have more than 20 characters")
   String phone,
   @NotBlank(message = "The field password must be provided")
   @Size(min = 8, message = "The password must contain at least 8 characters")
   String password
) {
}
