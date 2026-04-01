package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.ChangeEmailRequest;
import com.ppossatto.librarymanager.dto.request.ChangeRoleRequest;
import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.request.UpdatePasswordRequest;
import com.ppossatto.librarymanager.dto.request.UpdateUserRequest;
import com.ppossatto.librarymanager.dto.response.ChangeEmailResponse;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import com.ppossatto.librarymanager.dto.response.UpdateUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsersService {
  CreateAccountResponse createAccount(CreateAccountRequest request);

  Page<GetUsersResponse> getAllUsers(Pageable pageable);

  GetUserResponse getUser(UUID userId);

  UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request);

  void updatePassword(UUID userId, UpdatePasswordRequest request);

  void softDeleteUser(UUID userId);

  void blockUser(UUID userId);

  void changeUserRole(UUID userId, ChangeRoleRequest request);

  ChangeEmailResponse changeEmail(UUID userId, ChangeEmailRequest request);
}
