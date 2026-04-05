package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CreateAuthorResponse(
   UUID id,
   String name,
   LocalDate birthDate,
   String nationality,
   String biography
) {
}
