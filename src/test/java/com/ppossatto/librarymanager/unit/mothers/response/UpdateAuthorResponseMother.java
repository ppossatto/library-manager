package com.ppossatto.librarymanager.unit.mothers.response;

import com.ppossatto.librarymanager.dto.response.UpdateAuthorResponse;

import java.time.LocalDate;
import java.util.UUID;

public class UpdateAuthorResponseMother {

  public static UpdateAuthorResponse updateAuthorResponseOk(){
    return UpdateAuthorResponse.builder()
       .id(UUID.fromString("ed67cda5-51e4-4047-a133-0942da7032c0"))
       .name("John")
       .birthDate(LocalDate.of(1980, 1, 1))
       .nationality("American")
       .biography("Updated biography")
       .build();
  }

  public static UpdateAuthorResponse updateAuthorResponseWithoutUpdatedNameOk(){
    return UpdateAuthorResponse.builder()
       .id(UUID.fromString("ed67cda5-51e4-4047-a133-0942da7032c0"))
       .name("J. K. Rowling")
       .birthDate(LocalDate.of(1980, 1, 1))
       .nationality("American")
       .biography("Updated biography")
       .build();
  }

  public static UpdateAuthorResponse updateAuthorResponseWithOnlyUpdatedNameOk(){
    return UpdateAuthorResponse.builder()
       .id(UUID.fromString("4be84d9a-dff1-4e6a-81e5-cd824029d5ee"))
       .name("John")
       .birthDate(LocalDate.of(1965, 7, 31))
       .nationality("British")
       .biography("Biography of Rowling")
       .build();
  }
}
