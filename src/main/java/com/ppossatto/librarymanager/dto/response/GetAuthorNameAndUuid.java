package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetAuthorNameAndUuid(
   UUID authorId,
   String authorName
) {
}
