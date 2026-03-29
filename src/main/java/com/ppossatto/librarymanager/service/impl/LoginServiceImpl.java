package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.request.LoginRequest;
import com.ppossatto.librarymanager.dto.response.LoginResponse;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.security.service.JwtService;
import com.ppossatto.librarymanager.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Value("${security.jwt.expiration}")
  private Long expiration;

  @Override
  public LoginResponse doLogin(LoginRequest request) {
    try{
      log.debug("Authenticating login request");
      Authentication authentication = authenticationManager.authenticate(
         new UsernamePasswordAuthenticationToken(request.email(), request.password())
      );
      log.info("User authenticated!");
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      String token = jwtService.generateToken(userDetails);
      log.debug("Token generated");

      return LoginResponse.builder()
         .token(token)
         .tokenType("Bearer")
         .expiresIn(expiration)
         .build();
    } catch (BadCredentialsException bce){
      throw new CoreException(CoreExceptionType.BAD_CREDENTIALS_EXCEPTION, bce);
    } catch (Exception e){
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
