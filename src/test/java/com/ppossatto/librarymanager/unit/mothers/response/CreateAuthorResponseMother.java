package com.ppossatto.librarymanager.unit.mothers.response;

import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;

import java.time.LocalDate;
import java.util.UUID;

public class CreateAuthorResponseMother {

  public static CreateAuthorResponse createAuthorResponseOk(){
    return CreateAuthorResponse.builder()
       .id(UUID.fromString("4b0caf56-dc3d-421d-a9a3-2611684a1608"))
       .name("Stephen King")
       .birthDate(LocalDate.of(1947, 9, 21))
       .nationality("American")
       .biography("Biography of Stephen King")
       .build();
  }
}
