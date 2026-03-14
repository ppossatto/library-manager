package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.Year;
import java.util.Set;

@Builder
public record GetBookBasicResponse(
   long id,
   String title,
   Set<GetBookBasicAuthorDataResponse> authors,
   String edition,
   Year publishedYear
) {
}

