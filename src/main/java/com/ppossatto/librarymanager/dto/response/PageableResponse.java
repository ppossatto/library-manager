package com.ppossatto.librarymanager.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageableResponse<T>(
   List<T> content,
   long totalElements,
   int totalPages
) {
  public static <T> PageableResponse<T> from(Page<T> page) {
    return PageableResponse.<T>builder()
       .content(page.getContent())
       .totalElements(page.getTotalElements())
       .totalPages(page.getTotalPages())
       .build();
  }
}
