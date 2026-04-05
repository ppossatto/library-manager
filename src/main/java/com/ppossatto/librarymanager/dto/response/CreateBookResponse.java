package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.Year;
import java.util.Set;

@Builder
public record CreateBookResponse(
   Long id,
   String title,
   String isbn,
   Set<GetAuthorNameAndUuid> authors,
   Year publishYear,
   String edition,
   String synopsis,
   Integer totalPages
) {
}
