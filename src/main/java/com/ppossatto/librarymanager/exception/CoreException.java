package com.ppossatto.librarymanager.exception;

import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {

  private final CoreExceptionType exceptionType;

  public CoreException(CoreExceptionType exceptionType) {
    super(exceptionType.getDetails());
    this.exceptionType = exceptionType;
  }

  public CoreException(CoreExceptionType exceptionType, Throwable cause) {
    super(exceptionType.getDetails(), cause);
    this.exceptionType = exceptionType;
  }

  public CoreException(CoreExceptionType exceptionType, String value) {
    super(String.format(exceptionType.getDetails(), value));
    this.exceptionType = exceptionType;
  }

  public CoreException(CoreExceptionType exceptionType, String value, Throwable cause) {
    super(String.format(exceptionType.getDetails(), value), cause);
    this.exceptionType = exceptionType;
  }
}