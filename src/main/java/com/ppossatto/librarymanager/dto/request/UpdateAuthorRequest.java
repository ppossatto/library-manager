package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateAuthorRequest(
   @Size(max = 150, message = "The name cannot have more than 150 characters")
   String name,
   @Past(message = "The author birth date must be in a past date")
   LocalDate birthDate,
   @Size(max = 100, message = "The nationality cannot have more than 100 characters")
   String nationality,
   String biography
) {
}
