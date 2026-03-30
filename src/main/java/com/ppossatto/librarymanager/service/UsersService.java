package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;
import com.ppossatto.librarymanager.dto.response.GetUserResponse;
import com.ppossatto.librarymanager.dto.response.GetUsersResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsersService {
  CreateAccountResponse createAccount(CreateAccountRequest request);

  Page<GetUsersResponse> getAllUsers(Pageable pageable);

  GetUserResponse getUser(UUID userId);
}
