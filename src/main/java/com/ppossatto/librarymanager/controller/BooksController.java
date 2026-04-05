package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.CreateBookRequest;
import com.ppossatto.librarymanager.dto.request.UpdateBookRequest;
import com.ppossatto.librarymanager.dto.response.AddAuthorToBookResponse;
import com.ppossatto.librarymanager.dto.response.CreateBookResponse;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.dto.response.GetBookResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.dto.response.UpdateBookResponse;
import com.ppossatto.librarymanager.service.BooksService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BooksController {

  private final BooksService service;

  private static final String TRACE_ID = "traceId";

  @GetMapping
  public ResponseEntity<PageableResponse<GetBookBasicResponse>> getBooksPageable(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size,
     @RequestParam(required = false) String title,
     @RequestParam(required = false) String isbn
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("""
          Get all books requested...
          Page number: {},
          Page size: {},
          Title: {},
          ISBN: {}
          """,
       page, size,
       title != null ? title : "-",
       isbn != null ? isbn : "-");

    Page<GetBookBasicResponse> responsePage = service.getAllBooks(PageRequest.of(page, size), title, isbn);

    return ResponseEntity.ok(PageableResponse.from(responsePage));
  }

  @GetMapping("{id}")
  public ResponseEntity<GetBookResponse> getBookById(
     @PathVariable("id") Long bookId
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Get book with id [{}] requested...", bookId);
    return ResponseEntity.ok(service.getBookById(bookId));
  }

  @PostMapping
  public ResponseEntity<CreateBookResponse> createBook(
     @RequestBody @Valid CreateBookRequest request
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Create book resource requested...");
    CreateBookResponse createBookResponse = service.createBook(request);
    URI responseUri = URI.create("/api/v1/books/" + createBookResponse.id());
    return ResponseEntity.created(responseUri).body(createBookResponse);
  }

  @PatchMapping("{id}")
  public ResponseEntity<UpdateBookResponse> updateBook(
     @RequestBody @Valid UpdateBookRequest request,
     @PathVariable("id") Long bookId
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Update book resource requested for book with ID [{}]...", bookId);
    return ResponseEntity.ok(service.updateBook(request, bookId));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteBook(
     @PathVariable("id") Long bookId
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Delete book with ID [{}]...", bookId);
    service.deleteBookById(bookId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("{id}/authors/{authorId}")
  public ResponseEntity<AddAuthorToBookResponse> addAuthorToBook(
     @PathVariable("id") Long bookId,
     @PathVariable("authorId") UUID authorId
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Add author with ID [{}] resource requested for book with ID [{}]...", authorId, bookId);
    AddAuthorToBookResponse response = service.addAuthorToBook(bookId, authorId);
    URI responseUri = URI.create("/api/v1/books/" + response.id());
    return ResponseEntity.created(responseUri).body(response);
  }

  @DeleteMapping("{id}/authors/{authorId}")
  public ResponseEntity<Void> deleteAuthorFromBook(
     @PathVariable("id") Long bookId,
     @PathVariable("authorId") UUID authorId
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Delete author with ID [{}] resource requested for book with ID [{}]...", authorId, bookId);
    service.deleteAuthorFromBook(bookId, authorId);
    return ResponseEntity.noContent().build();
  }
}
