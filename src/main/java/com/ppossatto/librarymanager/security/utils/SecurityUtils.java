package com.ppossatto.librarymanager.security.utils;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityUtils {

  private static final String ROLE_USER = "ROLE_USER";
  private static final String ROLE_LIBRARIAN = "ROLE_LIBRARIAN";

  public Authentication getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new CoreException(CoreExceptionType.NOT_LOGGED_IN_EXCEPTION);
    }
    return authentication;
  }

  public boolean isLibrarian() {
    return getAuthentication().getAuthorities().stream()
       .anyMatch(a -> Objects.equals(a.getAuthority(), ROLE_LIBRARIAN));
  }

  public String getAuthenticatedEmail() {
    return getAuthentication().getName();
  }
}
