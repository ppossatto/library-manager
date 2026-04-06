package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateReservationResponse(
   Long id,
   GetBookIdAndTitle book,
   GetUserIdAndName user,
   LocalDate expectedDevolutionDate,
   String observations
) {
}
