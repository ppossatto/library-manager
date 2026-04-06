package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GetReservationResponse(
   Long id,
   GetBookIdAndTitle book,
   GetUserIdAndName user,
   LocalDate reservationDate,
   LocalDate expectedDevolutionDate,
   LocalDate devolutionDate,
   String status,
   String observations
) {
}
