package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.CreateAuthorRequest;
import com.ppossatto.librarymanager.dto.request.UpdateAuthorRequest;
import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetBookResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.dto.response.UpdateAuthorResponse;
import com.ppossatto.librarymanager.service.AuthorsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthorsController {

  private final AuthorsService authorsService;

  private static final String TRACE_ID = "traceId";

  @GetMapping
  public ResponseEntity<PageableResponse<GetAllAuthorsResponse>> getAllAuthors(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size,
     @RequestParam(required = false) String name
  ) {
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("""
          Get all authors requested...
          Page number: {},
          Page size: {},
          Author name: {}
          """,
       page, size,
       name != null ? name : "-");
    Page<GetAllAuthorsResponse> response = authorsService.getAllAuthors(PageRequest.of(page, size), name);

    return ResponseEntity.ok(PageableResponse.from(response));
  }

  @GetMapping("{id}")
  public ResponseEntity<GetAuthorResponse> getAuthor(
     @PathVariable("id") UUID authorId
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Get author with id [{}] requested...",authorId);
    return ResponseEntity.ok(authorsService.getAuthorById(authorId));
  }

  @PostMapping
  public ResponseEntity<CreateAuthorResponse> createAuthor(
     @Valid @RequestBody CreateAuthorRequest request
     ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Create author requested...");
    CreateAuthorResponse response = authorsService.createAuthor(request);
    return ResponseEntity.created(URI.create("/api/v1/authors/" + response.id())).body(response);
  }

  @PatchMapping("{id}")
  public ResponseEntity<UpdateAuthorResponse> updateAuthor(
     @PathVariable("id") UUID authorId,
     @Valid @RequestBody UpdateAuthorRequest request
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Update author with ID  [{}] requested...", authorId);
    return ResponseEntity.ok(authorsService.updateAuthor(request, authorId));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteAuthor(
     @PathVariable("id") UUID authorId
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Delete author with id [{}] requested...",authorId);
    authorsService.deleteAuthorById(authorId);
    return ResponseEntity.noContent().build();
  }

  // DELETE /api/v1/authors/{id} → hard delete (ROLE_LIBRARIAN)
  //   - Only if author has no books associated
}
