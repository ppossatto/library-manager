package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangeRoleRequest(
   @NotBlank(message = "The role field must be provided")
   String role
) {
}
