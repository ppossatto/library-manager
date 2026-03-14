package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetBookBasicAuthorDataResponse(
   UUID id,
   String name
) {
}
