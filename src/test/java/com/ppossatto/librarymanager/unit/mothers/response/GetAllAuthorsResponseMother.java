package com.ppossatto.librarymanager.unit.mothers.response;

import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;

import java.util.List;
import java.util.UUID;

public class GetAllAuthorsResponseMother {

  public static List<GetAllAuthorsResponse> getAllAuthorsResponses() {
    return List.of(getAllAuthorsOneOk(), getAllAuthorsTwoOk());
  }

  public static GetAllAuthorsResponse getAllAuthorsOneOk(){
    return GetAllAuthorsResponse.builder()
       .id(UUID.fromString("fb1c26ad-a614-49b9-916e-c49aeeef28b7"))
       .name("J. R. R. Tolkien")
       .build();
  }

  public static GetAllAuthorsResponse getAllAuthorsTwoOk(){
    return GetAllAuthorsResponse.builder()
       .id(UUID.fromString("4be84d9a-dff1-4e6a-81e5-cd824029d5ee"))
       .name("J. K. Rowling")
       .build();
  }
}
