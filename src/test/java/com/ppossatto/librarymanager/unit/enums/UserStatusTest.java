package com.ppossatto.librarymanager.unit.enums;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;
import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
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
class UserStatusTest {

  // Arrange
  private static Stream<Arguments> validUserCodes(){
    return Stream.of(
       Arguments.of("active", UserStatus.ACTIVE),
       Arguments.of("aCtIvE", UserStatus.ACTIVE),
       Arguments.of("ACTIVE", UserStatus.ACTIVE),
       Arguments.of("active\n", UserStatus.ACTIVE),
       Arguments.of("active\t", UserStatus.ACTIVE),
       Arguments.of("active ", UserStatus.ACTIVE),
       Arguments.of("inactive", UserStatus.INACTIVE),
       Arguments.of("iNaCtIvE", UserStatus.INACTIVE),
       Arguments.of("INACTIVE", UserStatus.INACTIVE),
       Arguments.of("inactive\n", UserStatus.INACTIVE),
       Arguments.of("inactive\t", UserStatus.INACTIVE),
       Arguments.of("inactive ", UserStatus.INACTIVE),
       Arguments.of("blocked", UserStatus.BLOCKED),
       Arguments.of("bLoCkEd", UserStatus.BLOCKED),
       Arguments.of("BLOCKED", UserStatus.BLOCKED),
       Arguments.of("blocked ", UserStatus.BLOCKED),
       Arguments.of("blocked\n", UserStatus.BLOCKED),
       Arguments.of("blocked\t", UserStatus.BLOCKED)
    );
  }

  // Arrange
  private static Stream<Arguments> invalidUserCodes(){
    return Stream.of(
       Arguments.of("test", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("wrong", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("ERROR", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("\t", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of(null, CoreExceptionType.NULL_CODE_EXCEPTION),
       Arguments.of("", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("\n", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("12345", CoreExceptionType.UNSUPPORTED_USER_STATUS),
       Arguments.of("sudo", CoreExceptionType.UNSUPPORTED_USER_STATUS)
    );
  }

  @ParameterizedTest
  @MethodSource("validUserCodes")
  @DisplayName("""
     GIVEN a list of valid user codes
     WHEN getStatusByCode method is called
     THEN the enum correspondent must return correctly
     """)
  void testValidUserStatusConversions(String code, UserStatus expected){
    // Act && Assert
    UserStatus userStatus = assertDoesNotThrow(
       () -> UserStatus.getStatusByCode(code)
    );

    assertEquals(expected, userStatus);
  }

  @ParameterizedTest
  @MethodSource("invalidUserCodes")
  @DisplayName("""
     GIVEN a list of invalid codes
     WHEN getStatusByCode method is called
     THEN the CoreException must be thrown
     AND the exceptionType must be correspondent
     """)
  void testInvalidUserStatusConversionsException(String code, CoreExceptionType expected){
    // Act && Assert
    CoreException exception = assertThrowsExactly(
       CoreException.class,
       () -> UserStatus.getStatusByCode(code)
    );

    assertEquals(expected, exception.getExceptionType());
  }
}
