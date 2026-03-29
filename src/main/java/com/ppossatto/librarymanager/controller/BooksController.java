package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.service.BooksService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BooksController {

  private final BooksService service;

  @GetMapping
  public ResponseEntity<PageableResponse<GetBookBasicResponse>> getBooksPageable(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size
  ) {
    UUID traceId = UUID.randomUUID();
    log.info("""
          Get all books requested...
          Page number: {},
          Page size: {},
          Trace ID: {}
          """,
       page, size, traceId);

    Page<GetBookBasicResponse> responsePage = service.getAllBooks(PageRequest.of(page, size), traceId);

    return responsePage.hasContent()
       ? ResponseEntity.ok(PageableResponse.from(responsePage))
       : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
