package com.ppossatto.librarymanager.mapper;

import com.ppossatto.librarymanager.dto.domain.AuthorDto;
import com.ppossatto.librarymanager.dto.domain.BookDto;
import com.ppossatto.librarymanager.entity.AuthorsEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthorsMapper {

  public static AuthorDto toDto(AuthorsEntity entity) {
    if (entity == null) {
      return null;
    }
    return AuthorDto.builder()
       .id(entity.getAuthorId())
       .name(entity.getAuthorName())
       .books(
          entity.getBooksEntity()
             .stream()
             .map(book -> BookDto.builder()
                .id(book.getBookId())
                .title(book.getBookTitle())
                .isbn(book.getBookIsbn())
                .publishYear(book.getBookPublishYear())
                .edition(book.getBookEdition())
                .synopsis(book.getBookSynopsis())
                .totalPages(book.getBookTotalPages())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build())
             .collect(Collectors.toSet())
       )
       .birthDate(entity.getAuthorBirthDate())
       .nationality(entity.getAuthorNationality())
       .biography(entity.getAuthorBiography())
       .createdAt(entity.getCreatedAt())
       .updatedAt(entity.getUpdatedAt())
       .build();
  }
}
