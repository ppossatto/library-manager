package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateUserResponse(
   UUID id,
   String name,
   String phone
) {
}
