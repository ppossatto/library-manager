package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.CreateAccountRequest;
import com.ppossatto.librarymanager.dto.response.CreateAccountResponse;

public interface UsersService {
  CreateAccountResponse createAccount(CreateAccountRequest request);
}
