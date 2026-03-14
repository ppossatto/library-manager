package com.ppossatto.librarymanager.dto.domain.enums;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum UserStatus {
  ACTIVE("active"),
  INACTIVE("inactive"),
  BLOCKED("blocked");

  private final String code;

  public static UserStatus getStatusByCode(String code) {
    if (code == null) {
      throw new CoreException(CoreExceptionType.NULL_CODE_EXCEPTION, new NullPointerException());
    }
    return Arrays.stream(UserStatus.values())
       .filter(status -> status.code.equalsIgnoreCase(code.trim()))
       .findFirst()
       .orElseThrow(() -> new CoreException(
          CoreExceptionType.UNSUPPORTED_USER_STATUS, code
       ));
  }
}
