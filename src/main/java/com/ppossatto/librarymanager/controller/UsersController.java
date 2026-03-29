package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UsersController {

  private final UsersService service;

  @PostMapping
  public ResponseEntity<CreateAccountResponse> createAccount(
     @RequestBody
     @Valid
     CreateAccountRequest request
  ){
    log.info("Create account endpoint");
    CreateAccountResponse response = service.createAccount(request);
    URI userUri = URI.create("/api/v1/users/" + response.id());
    return ResponseEntity.created(userUri).body(response);
  }
}
