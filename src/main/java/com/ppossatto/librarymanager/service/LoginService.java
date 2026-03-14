package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.request.LoginRequest;
import com.ppossatto.librarymanager.dto.response.LoginResponse;

public interface LoginService {

  LoginResponse doLogin(LoginRequest request);
}
