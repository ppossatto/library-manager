package com.ppossatto.librarymanager.unit.mothers;

import com.ppossatto.librarymanager.dto.domain.AuthorDto;
import com.ppossatto.librarymanager.dto.domain.BookDto;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;
import java.util.UUID;

public class BookDtoMother {

  public static BookDto getBookDto1(){
    return BookDto.builder()
       .id(1L)
       .title("Clean Code")
       .isbn("978-0132350884")
       .authors(
          Set.of(
             AuthorDto.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .name("Robert C. Martin")
                .birthDate(LocalDate.of(1952, 12, 5))
                .nationality("American")
                .biography("Software engineer and author, known as Uncle Bob.")
                .build()
          )
       )
       .publishYear(Year.of(2008))
       .edition("1st Edition")
       .synopsis("A handbook of agile software craftsmanship.")
       .totalPages(431)
       .build();
  }

  public static BookDto getBookDto2(){
    return BookDto.builder()
       .id(2L)
       .title("Effective Java")
       .isbn("978-0134685991")
       .authors(
          Set.of(
             AuthorDto.builder()
                .id(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"))
                .name("Joshua Bloch")
                .birthDate(LocalDate.of(1961, 8, 28))
                .nationality("American")
                .biography("Software engineer, worked at Sun Microsystems and Google.")
                .build()
          )
       )
       .publishYear(Year.of(2018))
       .edition("3rd Edition")
       .synopsis("Best practices for the Java programming language.")
       .totalPages(412)
       .build();
  }

  public static BookDto getBookDto3(){
    return BookDto.builder()
       .id(3L)
       .title("Design Patterns")
       .isbn("978-0201633610")
       .authors(
          Set.of(
             AuthorDto.builder()
                .id(UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012"))
                .name("Erich Gamma")
                .birthDate(LocalDate.of(1961, 3, 13))
                .nationality("Swiss")
                .biography("Computer scientist and co-author of Design Patterns.")
                .build(),
             AuthorDto.builder()
                .id(UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123"))
                .name("Richard Helm")
                .birthDate(LocalDate.of(1962, 1, 1))
                .nationality("Australian")
                .biography("Software engineer and co-author of Design Patterns.")
                .build()
          )
       )
       .publishYear(Year.of(1994))
       .edition("1st Edition")
       .synopsis("Elements of reusable object-oriented software.")
       .totalPages(395)
       .build();
  }
}
