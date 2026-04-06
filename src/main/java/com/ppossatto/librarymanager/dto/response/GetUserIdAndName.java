package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetUserIdAndName(
   UUID id,
   String name
) {
}
