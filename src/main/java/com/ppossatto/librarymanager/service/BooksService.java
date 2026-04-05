package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.CreateBookRequest;
import com.ppossatto.librarymanager.dto.request.UpdateBookRequest;
import com.ppossatto.librarymanager.dto.response.AddAuthorToBookResponse;
import com.ppossatto.librarymanager.dto.response.CreateBookResponse;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.dto.response.GetBookResponse;
import com.ppossatto.librarymanager.dto.response.UpdateBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BooksService {

  Page<GetBookBasicResponse> getAllBooks(Pageable pageable, String title, String isbn);

  GetBookResponse getBookById(Long bookId);

  CreateBookResponse createBook(CreateBookRequest request);

  UpdateBookResponse updateBook(UpdateBookRequest request, Long bookId);

  void deleteBookById(Long bookId);

  AddAuthorToBookResponse addAuthorToBook(Long bookId, UUID authorId);

  void deleteAuthorFromBook(Long bookId, UUID authorId);
}
