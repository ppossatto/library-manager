package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.service.UsersService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UsersController {

  private final UsersService service;

  @PostMapping
  public ResponseEntity<CreateAccountResponse> createAccount(
     @RequestBody
     @Valid
     CreateAccountRequest request
  ) {
    log.info("Create account endpoint");
    CreateAccountResponse response = service.createAccount(request);
    URI userUri = URI.create("/api/v1/users/" + response.id());
    return ResponseEntity.created(userUri).body(response);
  }

  @GetMapping
  public ResponseEntity<PageableResponse<GetUsersResponse>> getUsers(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size
  ) {
    Page<GetUsersResponse> responsePage = service.getAllUsers(PageRequest.of(page, size));

    return responsePage.hasContent()
       ? ResponseEntity.ok(PageableResponse.from(responsePage))
       : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetUserResponse> getSingleUser(
     @PathVariable("id") UUID userId
  ) {
    GetUserResponse response = service.getUser(userId);

    return response == null ?
       ResponseEntity.status(HttpStatus.NOT_FOUND).build() :
       ResponseEntity.ok(response);
  }
}
