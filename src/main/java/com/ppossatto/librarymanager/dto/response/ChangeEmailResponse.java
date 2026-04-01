package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record ChangeEmailResponse(
   String email,
   String newToken,
   String tokenType,
   Long expiresIn
) {
}
