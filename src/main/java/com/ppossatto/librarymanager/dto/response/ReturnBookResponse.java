package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReturnBookResponse(
   Long reservationId,
   LocalDate expectedDevolutionDate,
   LocalDate devolutionDate,
   boolean returnedLate
) {}
