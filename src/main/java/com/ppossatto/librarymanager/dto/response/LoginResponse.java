package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
   String token,
   String tokenType,
   Long expiresIn
) {
}
