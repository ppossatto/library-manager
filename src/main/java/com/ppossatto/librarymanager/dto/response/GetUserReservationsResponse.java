package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record GetUserReservationsResponse(
   Long id,
   GetBookIdAndTitle book,
   String status
) {
}
