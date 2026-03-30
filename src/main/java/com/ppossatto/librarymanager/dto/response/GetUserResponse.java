package com.ppossatto.librarymanager.dto.response;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record GetUserResponse(
   UUID id,
   String name,
   String email,
   String phone,
   UserStatus status,
   LocalDateTime inactiveDateTime,
   Set<String> roles,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {
}
