package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.ChangeEmailRequest;
import com.ppossatto.librarymanager.dto.request.ChangeRoleRequest;
import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.request.UpdatePasswordRequest;
import com.ppossatto.librarymanager.dto.request.UpdateUserRequest;
import com.ppossatto.librarymanager.dto.response.ChangeEmailResponse;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.dto.response.UpdateUserResponse;
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

  @PatchMapping("{id}")
  public ResponseEntity<UpdateUserResponse> updateUser(
     @Valid
     @RequestBody
     UpdateUserRequest updateUserRequest,
     @PathVariable("id") UUID userId
  ){
    return ResponseEntity.ok(service.updateUser(userId, updateUserRequest));
  }

  @PatchMapping("{id}/password")
  public ResponseEntity<Void> changePassword(
     @PathVariable("id") UUID userId,
     @Valid
     @RequestBody
     UpdatePasswordRequest updatePasswordRequest
  ){
    service.updatePassword(userId, updatePasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteUser(
     @PathVariable("id") UUID userId
  ){
    service.softDeleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("{id}/block")
  public ResponseEntity<Void> blockUser(
     @PathVariable("id") UUID userId
  ){
    service.blockUser(userId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("{id}/role")
  public ResponseEntity<Void> changeUserRole(
     @PathVariable("id") UUID userId,
     @Valid @RequestBody ChangeRoleRequest changeRoleRequest
     ){
    service.changeUserRole(userId, changeRoleRequest);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("{id}/email")
  public ResponseEntity<ChangeEmailResponse> changeEmail(
     @PathVariable("id") UUID userId,
     @Valid @RequestBody ChangeEmailRequest changeEmailRequest
  ){
    return ResponseEntity.ok(service.changeEmail(userId, changeEmailRequest));
  }

  // TODO: Cron job to delete users inactive for more than 6 months (users must not have any active book)
    // No endpoint needed
}
