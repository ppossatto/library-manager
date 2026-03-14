package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.LoginRequest;
import com.ppossatto.librarymanager.dto.response.LoginResponse;
import com.ppossatto.librarymanager.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

  private final LoginService service;

  @PostMapping
  public ResponseEntity<LoginResponse> doLogin(
     @RequestBody
     @Valid
     LoginRequest request
  ) {
    return ResponseEntity.ok().body(service.doLogin(request));
  }
}
