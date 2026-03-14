package com.ppossatto.librarymanager.dto.domain.enums;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ReservationStatus {

  ACTIVE("active"),
  RETURNED("returned"),
  OVERDUE("overdue");

  private final String code;

  public static ReservationStatus getReservationStatusByCode(String code) {
    if (code == null) {
      throw new CoreException(CoreExceptionType.NULL_CODE_EXCEPTION, new NullPointerException());
    }
    return Arrays.stream(ReservationStatus.values())
       .filter(status -> status.code.equalsIgnoreCase(code.trim()))
       .findFirst()
       .orElseThrow(
          () -> new CoreException(
             CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS, code
          )
       );
  }
}
