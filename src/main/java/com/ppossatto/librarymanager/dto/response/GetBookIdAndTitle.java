package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;

@Builder
public record GetBookIdAndTitle(
   Long id,
   String title
) {
}
