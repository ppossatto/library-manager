package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
   @NotBlank(message = "The field name must be provided")
   @Size(max = 150, message = "The name cannot have more than 150 characters")
   String name,
   @Size(max = 20, message = "The phone number cannot have more than 20 characters")
   String phone
) {
}
