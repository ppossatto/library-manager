package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record GetAuthorResponse(
   UUID id,
   String name,
   Set<GetBookIdAndTitle> books,
   LocalDate birthDate,
   String nationality,
   String biography
) {
}
