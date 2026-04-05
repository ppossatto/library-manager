package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UpdateAuthorResponse(
   UUID id,
   String name,
   LocalDate birthDate,
   String nationality,
   String biography
) {
}
