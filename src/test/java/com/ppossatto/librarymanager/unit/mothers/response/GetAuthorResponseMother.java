package com.ppossatto.librarymanager.unit.mothers.response;

import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetBookIdAndTitle;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class GetAuthorResponseMother {

  public static GetAuthorResponse getAuthorOkResponse() {
    return GetAuthorResponse.builder()
       .id(UUID.fromString("fb1c26ad-a614-49b9-916e-c49aeeef28b7"))
       .name("J. R. R. Tolkien")
       .books(Set.of(
          GetBookIdAndTitle.builder()
             .id(1L)
             .title("Lord of the Rings")
             .build()
       ))
       .birthDate(LocalDate.of(1892, 1, 3))
       .nationality("South African")
       .biography("Biography of Tolkien")
       .build();
  }
}
