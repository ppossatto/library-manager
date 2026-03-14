package com.ppossatto.librarymanager.unit.service;

import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.BooksRepository;
import com.ppossatto.librarymanager.service.impl.GetAllBooksServiceImpl;
import com.ppossatto.librarymanager.unit.mothers.BooksEntityDaoMother;
import jakarta.persistence.NoResultException;
import jakarta.persistence.QueryTimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllBooksServiceTest {

  @Mock
  private BooksRepository repository;

  @InjectMocks
  private GetAllBooksServiceImpl service;

  private static Pageable pageableMock;
  private static UUID uuidMock;

  @BeforeAll
  static void setUp(){
    pageableMock = mock(Pageable.class);
    uuidMock = UUID.fromString("133fcdea-84f2-4dbb-98a4-c8a8d58cc719");
  }

  @Test
  @DisplayName("""
     GIVEN a get all books request
     WHEN the database returns the books paginated
     THEN validate response
     AND injection usage
     """)
  void testGetAllBooksCorrectly_validateCorrectResponseAndInjections(){
    //Arrange
    when(repository.getAll(pageableMock)).thenReturn(BooksEntityDaoMother.booksPageResponseOk());

    //Act && Assert
    Page<GetBookBasicResponse> response = assertDoesNotThrow(
       () -> service.getAllBooks(pageableMock, uuidMock)
    );

    assertNotNull(response);
    assertEquals(3L, response.getTotalElements());
    verify(repository).getAll(pageableMock);
  }

  @Test
  @DisplayName("""
     GIVEN a get all books request
     WHEN the database times out
     THEN validate exception
     AND injection usage
     """)
  void testGetAllBooksTimeout_validateCorrectExceptionAndInjections(){
    //Arrange
    when(repository.getAll(pageableMock)).thenThrow(new QueryTimeoutException());

    //Act && Assert
    CoreException exception = assertThrowsExactly(
       CoreException.class,
       () -> service.getAllBooks(pageableMock, uuidMock)
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.GET_ALL_BOOKS_JPA_TIMEOUT_EXCEPTION, exception.getExceptionType());
    verify(repository).getAll(pageableMock);
  }

  @Test
  @DisplayName("""
     GIVEN a get all books request
     WHEN the database throws unhandled specific JPA exception
     THEN validate exception
     AND injection usage
     """)
  void testGetAllBooksJpaException_validateCorrectExceptionAndInjections(){
    //Arrange
    when(repository.getAll(pageableMock)).thenThrow(new NoResultException());

    //Act && Assert
    CoreException exception = assertThrowsExactly(
       CoreException.class,
       () -> service.getAllBooks(pageableMock, uuidMock)
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.GET_ALL_BOOKS_JPA_EXCEPTION, exception.getExceptionType());
    verify(repository).getAll(pageableMock);
  }

  @Test
  @DisplayName("""
     GIVEN a get all books request
     WHEN the database throws generic unhandled specific exception
     THEN validate exception
     AND injection usage
     """)
  void testGetAllBooksGenericException_validateCorrectExceptionAndInjections(){
    //Arrange
    when(repository.getAll(pageableMock)).thenThrow(new RuntimeException());

    //Act && Assert
    CoreException exception = assertThrowsExactly(
       CoreException.class,
       () -> service.getAllBooks(pageableMock, uuidMock)
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.GENERIC_ERROR, exception.getExceptionType());
    verify(repository).getAll(pageableMock);
  }
}
