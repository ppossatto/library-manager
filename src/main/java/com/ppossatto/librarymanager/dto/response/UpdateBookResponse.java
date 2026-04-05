package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.Year;

@Builder
public record UpdateBookResponse(
   Long id,
   String title,
   String isbn,
   Year publishYear,
   String edition,
   String synopsis,
   Integer totalPages
) {
}
