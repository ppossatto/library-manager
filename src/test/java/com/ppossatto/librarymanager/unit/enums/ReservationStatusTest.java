package com.ppossatto.librarymanager.unit.enums;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest
class ReservationStatusTest {

  // Arrange
  private static Stream<Arguments> validReservationCodes(){
    return Stream.of(
       Arguments.of("active", ReservationStatus.ACTIVE),
       Arguments.of("aCtIvE", ReservationStatus.ACTIVE),
       Arguments.of("ACTIVE", ReservationStatus.ACTIVE),
       Arguments.of("active\n", ReservationStatus.ACTIVE),
       Arguments.of("active\t", ReservationStatus.ACTIVE),
       Arguments.of("active ", ReservationStatus.ACTIVE),
       Arguments.of("returned", ReservationStatus.RETURNED),
       Arguments.of("rEtUrNeD", ReservationStatus.RETURNED),
       Arguments.of("RETURNED", ReservationStatus.RETURNED),
       Arguments.of("returned\n", ReservationStatus.RETURNED),
       Arguments.of("returned\t", ReservationStatus.RETURNED),
       Arguments.of("returned ", ReservationStatus.RETURNED),
       Arguments.of("overdue", ReservationStatus.OVERDUE),
       Arguments.of("oVeRdUe", ReservationStatus.OVERDUE),
       Arguments.of("OVERDUE", ReservationStatus.OVERDUE),
       Arguments.of("overdue ", ReservationStatus.OVERDUE),
       Arguments.of("overdue\n", ReservationStatus.OVERDUE),
       Arguments.of("overdue\t", ReservationStatus.OVERDUE)
    );
  }

  // Arrange
  private static Stream<Arguments> invalidReservationCodes(){
    return Stream.of(
       Arguments.of("inactive", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("wrong", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("ERROR", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("\t", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of(null, CoreExceptionType.NULL_CODE_EXCEPTION),
       Arguments.of("", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("\n", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("12345", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS),
       Arguments.of("sudo", CoreExceptionType.UNSUPPORTED_RESERVATION_STATUS)
    );
  }

  @ParameterizedTest
  @MethodSource("validReservationCodes")
  @DisplayName("""
     GIVEN a list of valid codes
     WHEN getReservationStatusByCode method is called
     THEN the enum correspondent must return correctly
     """)
  void testValidReservationStatusConversions(String code, ReservationStatus expected){
    // Act && Assert
    ReservationStatus returnedStatus = assertDoesNotThrow(
       () -> ReservationStatus.getReservationStatusByCode(code)
    );

    assertEquals(expected, returnedStatus);
  }

  @ParameterizedTest
  @MethodSource("invalidReservationCodes")
  @DisplayName("""
     GIVEN a list of invalid codes
     WHEN getReservationStatusByCode method is called
     THEN the CoreException must be thrown
     AND the exceptionType must be correspondent
     """)
  void testInvalidReservationStatusConversionsException(String code, CoreExceptionType expected){
    // Act && Assert
    CoreException exception = assertThrowsExactly(
       CoreException.class,
       () -> ReservationStatus.getReservationStatusByCode(code)
    );

    assertEquals(expected, exception.getExceptionType());
  }
}
