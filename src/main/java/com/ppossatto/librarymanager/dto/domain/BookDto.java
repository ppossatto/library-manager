package com.ppossatto.librarymanager.dto.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

@Builder
public record BookDto(
   long id,
   String title,
   String isbn,
   Set<AuthorDto> authors,
   Year publishYear,
   String edition,
   String synopsis,
   int totalPages,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {
}
