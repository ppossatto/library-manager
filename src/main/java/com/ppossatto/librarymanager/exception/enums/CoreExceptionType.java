package com.ppossatto.librarymanager.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CoreExceptionType {
  GENERIC_ERROR(
     "ERR-11463",
     SeverityType.ERROR,
     "Generic error, please check logs",
     HttpStatus.SERVICE_UNAVAILABLE
  ),
  GET_ALL_BOOKS_JPA_EXCEPTION(
     "ERR-33946",
     SeverityType.ERROR,
     "Generic error fetching books from the database",
     HttpStatus.INTERNAL_SERVER_ERROR
  ),
  GET_ALL_BOOKS_JPA_TIMEOUT_EXCEPTION(
     "ERR-50729",
     SeverityType.ERROR,
     "The query exceeded the timeout limit",
     HttpStatus.SERVICE_UNAVAILABLE
  ),
  UNSUPPORTED_RESERVATION_STATUS(
     "ERR-56697",
     SeverityType.WARNING,
     "Unsupported reservation status: '%s'",
     HttpStatus.BAD_REQUEST
  ),
  UNSUPPORTED_USER_STATUS(
     "ERR-60771",
     SeverityType.WARNING,
     "Unsupported user status: '%s'",
     HttpStatus.BAD_REQUEST
  ),
  NULL_CODE_EXCEPTION(
     "ERR-30741",
     SeverityType.WARNING,
     "Code values cannot be null",
     HttpStatus.BAD_REQUEST
  ),
  JWT_PARSE_EXCEPTION(
     "ERR-74139",
     SeverityType.ERROR,
     "Error while parsing JWT token",
     HttpStatus.INTERNAL_SERVER_ERROR
  ),
  JWT_CREATION_EXCEPTION(
     "ERR-14825",
     SeverityType.ERROR,
     "Error while creating JWT token",
     HttpStatus.INTERNAL_SERVER_ERROR
  ),
  EMAIL_ALREADY_EXISTS(
     "ERR-55128",
     SeverityType.WARNING,
     "The given email is already stored in the database",
     HttpStatus.CONFLICT
  ),
  ROLE_NOT_FOUND_EXCEPTION(
     "ERR-61427",
     SeverityType.ERROR,
     "The given role '%s' was not found",
     HttpStatus.NOT_FOUND
  ),
  BAD_CREDENTIALS_EXCEPTION(
     "ERR-49001",
     SeverityType.WARNING,
     "The given credentials are wrong",
     HttpStatus.UNAUTHORIZED
  );

  private final String errorCode;
  private final SeverityType severityType;
  private final String details;
  private final HttpStatus status;
}
