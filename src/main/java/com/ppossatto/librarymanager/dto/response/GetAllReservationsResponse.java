package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record GetAllReservationsResponse(
   Long id,
   GetUserIdAndName user,
   GetBookIdAndTitle book,
   String status
) {
}
