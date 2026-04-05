package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetAllAuthorsResponse(
   UUID id,
   String name
) {
}
