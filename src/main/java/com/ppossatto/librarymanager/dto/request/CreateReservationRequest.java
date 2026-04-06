package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateReservationRequest(
   @NotNull(message = "The user ID must be provided")
   UUID userId,
   @NotNull(message = "The book ID must be provided")
   Long bookId,
   String observations
) {
}
