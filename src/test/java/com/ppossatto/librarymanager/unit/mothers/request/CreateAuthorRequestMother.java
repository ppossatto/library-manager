package com.ppossatto.librarymanager.unit.mothers.request;

import com.ppossatto.librarymanager.dto.request.CreateAuthorRequest;

import java.time.LocalDate;

public class CreateAuthorRequestMother {

  public static CreateAuthorRequest createAuthorRequestOk() {
    return CreateAuthorRequest.builder()
       .name("Stephen King")
       .nationality("American")
       .biography("Biography of Stephen King")
       .birthDate(LocalDate.of(1947, 9, 21))
       .build();
  }
}
