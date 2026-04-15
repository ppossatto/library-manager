package com.ppossatto.librarymanager.unit.service;

import com.ppossatto.librarymanager.dto.request.LoginRequest;
import com.ppossatto.librarymanager.dto.response.LoginResponse;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.security.service.JwtService;
import com.ppossatto.librarymanager.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

  @InjectMocks
  LoginServiceImpl service;

  @Mock
  JwtService jwtService;

  @Mock
  AuthenticationManager authenticationManager;

  @BeforeEach
  void setUp(){
    ReflectionTestUtils.setField(service, "expiration", 9999L);
  }

  @Test
  @DisplayName("Test when user is authenticated")
  void testWhenUserIsAuthenticated(){
    // Arrange
    LoginRequest request = new LoginRequest("email@test.com", "password123");

    UserDetails userDetails = User.builder()
       .username("email@test.com")
       .password("password123")
       .authorities(List.of())
       .build();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    when(jwtService.generateToken(userDetails)).thenReturn("mocked-token");

    // Act
    LoginResponse response = service.doLogin(request);

    // Assert
    assertNotNull(response);
    assertEquals("mocked-token", response.token());
    assertEquals("Bearer", response.tokenType());
    assertEquals(9999L, response.expiresIn());
    verify(jwtService, times(1)).generateToken(userDetails);
    verifyNoMoreInteractions(jwtService);
    verify(authenticationManager, times(1)).authenticate(any());
    verifyNoMoreInteractions(authenticationManager);
  }

  @Test
  @DisplayName("Test when user authentication fails")
  void testWhenUserAuthenticationFails(){
    // Arrange
    LoginRequest request = new LoginRequest("email@test.com", "wrongpassword");

    when(authenticationManager.authenticate(any()))
       .thenThrow(new BadCredentialsException("Invalid credentials"));

    // Act && Assert
    CoreException exception = assertThrows(CoreException.class,
       () -> service.doLogin(request));

    assertEquals(CoreExceptionType.BAD_CREDENTIALS_EXCEPTION, exception.getExceptionType());
    verifyNoInteractions(jwtService);
  }

  @Test
  @DisplayName("Test when unexpected error happens")
  void testWhenUnexpectedErrorIsThrown(){
    // Arrange
    LoginRequest request = new LoginRequest("email@test.com", "wrongpassword");

    when(authenticationManager.authenticate(any()))
       .thenThrow(new RuntimeException("Unexpected exception"));

    // Act && Assert
    CoreException exception = assertThrows(CoreException.class,
       () -> service.doLogin(request));

    assertEquals(CoreExceptionType.GENERIC_ERROR, exception.getExceptionType());
    verifyNoInteractions(jwtService);
  }
}
