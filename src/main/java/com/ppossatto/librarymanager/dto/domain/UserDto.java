package com.ppossatto.librarymanager.dto.domain;

import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDto(
   UUID id,
   String name,
   String email,
   String phone,
   UserStatus status,
   LocalDateTime inactiveDateTime,
   String password,
   Set<String> roles,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {
}
