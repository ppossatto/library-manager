package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.CreateAuthorRequest;
import com.ppossatto.librarymanager.dto.request.UpdateAuthorRequest;
import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.UpdateAuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthorsService {

  Page<GetAllAuthorsResponse> getAllAuthors(Pageable pageable, String name);

  GetAuthorResponse getAuthorById(UUID id);

  CreateAuthorResponse createAuthor(CreateAuthorRequest request);

  UpdateAuthorResponse updateAuthor(UpdateAuthorRequest request, UUID authorId);

  void deleteAuthorById(UUID id);
}
