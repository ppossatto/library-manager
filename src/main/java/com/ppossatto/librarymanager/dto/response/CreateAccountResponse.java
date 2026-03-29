package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record CreateAccountResponse(
   String id,
   String name,
   String email,
   String phone,
   String status
) {
}
