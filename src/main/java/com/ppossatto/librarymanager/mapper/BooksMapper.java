package com.ppossatto.librarymanager.mapper;

import com.ppossatto.librarymanager.dto.domain.AuthorDto;
import com.ppossatto.librarymanager.dto.domain.BookDto;
import com.ppossatto.librarymanager.dto.response.GetBookBasicAuthorDataResponse;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.entity.BooksEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BooksMapper {

  public static BookDto toDto(BooksEntity entity) {
    if (entity == null) {
      return null;
    }
    return BookDto.builder()
       .id(entity.getBookId())
       .title(entity.getBookTitle())
       .isbn(entity.getBookIsbn())
       .authors(entity.getAuthorsEntity()
          .stream()
          .map(author -> AuthorDto.builder()
             .id(author.getAuthorId())
             .name(author.getAuthorName())
             .birthDate(author.getAuthorBirthDate())
             .nationality(author.getAuthorNationality())
             .biography(author.getAuthorBiography())
             .createdAt(author.getCreatedAt())
             .updatedAt(author.getUpdatedAt())
             .build())
          .collect(Collectors.toSet())
       )
       .publishYear(entity.getBookPublishYear())
       .edition(entity.getBookEdition())
       .synopsis(entity.getBookSynopsis())
       .totalPages(entity.getBookTotalPages())
       .createdAt(entity.getCreatedAt())
       .updatedAt(entity.getUpdatedAt())
       .build();
  }

  public static GetBookBasicResponse toBasicResponse(BookDto dto) {
    if (dto == null) {
      return null;
    }
    return GetBookBasicResponse.builder()
       .id(dto.id())
       .title(dto.title())
       .authors(
          dto.authors()
             .stream()
             .map(author -> GetBookBasicAuthorDataResponse
                .builder()
                .id(author.id())
                .name(author.name())
                .build()
             )
             .collect(Collectors.toSet())
       )
       .edition(dto.edition())
       .publishedYear(dto.publishYear())
       .build();
  }
}
