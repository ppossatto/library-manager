package com.ppossatto.librarymanager.unit.mothers.request;

import com.ppossatto.librarymanager.dto.request.UpdateAuthorRequest;

import java.time.LocalDate;

public class UpdateAuthorRequestMother {

  public static UpdateAuthorRequest updateAuthorRequestOk(){
    return UpdateAuthorRequest.builder()
       .name("John")
       .birthDate(LocalDate.of(1980, 1, 1))
       .nationality("American")
       .biography("Updated biography")
       .build();
  }

  public static UpdateAuthorRequest updateAuthorRequestWithoutNameOk(){
    return UpdateAuthorRequest.builder()
       .birthDate(LocalDate.of(1980, 1, 1))
       .nationality("American")
       .biography("Updated biography")
       .build();
  }

  public static UpdateAuthorRequest updateAuthorRequestWithSameNameOk(){
    return UpdateAuthorRequest.builder()
       .name("J. K. Rowling")
       .birthDate(LocalDate.of(1980, 1, 1))
       .nationality("American")
       .biography("Updated biography")
       .build();
  }

  public static UpdateAuthorRequest updateAuthorRequestOnlyNameOk(){
    return UpdateAuthorRequest.builder()
       .name("John")
       .build();
  }
}
