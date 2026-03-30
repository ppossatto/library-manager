package com.ppossatto.librarymanager.dto.response;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GetUsersResponse(
   UUID id,
   String name,
   String email,
   UserStatus status,
   LocalDateTime inactiveDateTime
) {
}
