package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.Year;
import java.util.Set;

@Builder
public record GetBookResponse(
   Long id,
   String title,
   String isbn,
   Set<String> authors,
   Year publishYear,
   String edition,
   String synopsis,
   Integer totalPages
) {
}
