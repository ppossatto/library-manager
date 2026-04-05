package com.ppossatto.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Year;
import java.util.Set;
import java.util.UUID;

@Builder
public record CreateBookRequest(
   @NotBlank(message = "The field title must be provided")
   @Size(max = 200, message = "The title title cannot have more than 200 characters")
   String title,
   @NotBlank(message = "The field ISBN must be provided")
   @Size(max = 20, message = "The ISBN cannot have more than 20 characters")
   @Pattern(regexp = "^(97[89])?\\d{9}([\\dX])$", message = "The ISBN format is invalid")
   String isbn,
   @NotEmpty(message = "At least one author must be provided")
   Set<UUID> authors,
   Year publishYear,
   @Size(max = 50, message = "The edition cannot have more than 50 characters")
   String edition,
   String synopsis,
   Integer totalPages
) {
}
