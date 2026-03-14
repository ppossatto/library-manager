package com.ppossatto.librarymanager.dto.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record AuthorDto(
   UUID id,
   String name,
   Set<BookDto> books,
   LocalDate birthDate,
   String nationality,
   String biography,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {
}
