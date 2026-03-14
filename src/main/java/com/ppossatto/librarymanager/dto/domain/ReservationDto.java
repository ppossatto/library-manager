package com.ppossatto.librarymanager.dto.domain;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(
   long id,
   UserDto user,
   BookDto book,
   LocalDate reservationDate,
   LocalDate expectedDevolutionDate,
   LocalDate realDevolutionDate,
   ReservationStatus status,
   String observations,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {
}
