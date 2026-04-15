package com.ppossatto.librarymanager.unit.service;

import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.UpdateAuthorResponse;
import com.ppossatto.librarymanager.entity.AuthorsEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.AuthorsRepository;
import com.ppossatto.librarymanager.service.impl.AuthorsServiceImpl;
import com.ppossatto.librarymanager.unit.mothers.entity.AuthorsEntityMother;
import com.ppossatto.librarymanager.unit.mothers.request.CreateAuthorRequestMother;
import com.ppossatto.librarymanager.unit.mothers.request.UpdateAuthorRequestMother;
import com.ppossatto.librarymanager.unit.mothers.response.CreateAuthorResponseMother;
import com.ppossatto.librarymanager.unit.mothers.response.GetAllAuthorsResponseMother;
import com.ppossatto.librarymanager.unit.mothers.response.GetAuthorResponseMother;
import com.ppossatto.librarymanager.unit.mothers.response.UpdateAuthorResponseMother;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorsServiceImplTest {

  @InjectMocks
  private AuthorsServiceImpl authorsService;

  @Mock
  private AuthorsRepository authorsRepository;

  private static Stream<Arguments> jpaExceptions(){
    return Stream.of(
       Arguments.of(
          new QueryTimeoutException("Mocked timeout exception"), CoreExceptionType.JPA_TIMEOUT_EXCEPTION
       ),
       Arguments.of(
          new PersistenceException("Mocked persistence exception"), CoreExceptionType.JPA_PERSISTENCE_EXCEPTION
       ),
       Arguments.of(
          new RuntimeException("Mocked persistence exception"), CoreExceptionType.GENERIC_ERROR
       )
    );
  }

  @Test
  @DisplayName("Get all authors correctly")
  void getAllAuthorsCorrectly() {
    // Arrange
    when(authorsRepository.findAuthorsWithFilter(anyString(), any(Pageable.class))).thenReturn(
       AuthorsEntityMother.authorsEntityPageOk()
    );

    // Act
    Page<GetAllAuthorsResponse> response = authorsService.getAllAuthors(mock(Pageable.class), "abc");

    // Assert
    assertNotNull(response);
    assertNotEquals(0, response.getTotalElements());
    assertNotEquals(0, response.getTotalPages());
    assertNotEquals(Collections.emptyList(), response.getContent());
    assertEquals(GetAllAuthorsResponseMother.getAllAuthorsResponses(), response.getContent());
    verify(authorsRepository).findAuthorsWithFilter(anyString(), any(Pageable.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Get no authors when db returns empty")
  void getAllAuthorsWhenDbReturnsEmpty() {
    // Arrange
    when(authorsRepository.findAuthorsWithFilter(anyString(), any(Pageable.class))).thenReturn(
       AuthorsEntityMother.authorsEntityPageEmpty()
    );

    // Act
    Page<GetAllAuthorsResponse> response = authorsService.getAllAuthors(mock(Pageable.class), "abc");

    // Assert
    assertNotNull(response);
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
    assertEquals(Collections.emptyList(), response.getContent());
    verify(authorsRepository).findAuthorsWithFilter(anyString(), any(Pageable.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @ParameterizedTest
  @MethodSource("jpaExceptions")
  @DisplayName("Verify core exception when exceptions are thrown")
  void verifyCoreExceptionWhenExceptionsAreThrown(Exception expectedException, CoreExceptionType exceptionType) {
    // Arrange
    when(authorsRepository.findAuthorsWithFilter(anyString(), any(Pageable.class))).thenThrow(
       expectedException
    );

    // Act
    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.getAllAuthors(mock(Pageable.class), "abc")
    );

    // Assert
    assertNotNull(exception);
    assertEquals(exceptionType, exception.getExceptionType());
    verify(authorsRepository).findAuthorsWithFilter(anyString(), any(Pageable.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Get single author correctly")
  void getSingleAuthorCorrectly() {
    when(authorsRepository.findById(any(UUID.class))).thenReturn(
       Optional.of(AuthorsEntityMother.authorsEntityOneOk())
    );

    GetAuthorResponse response = authorsService.getAuthorById(UUID.randomUUID());

    assertNotNull(response);
    assertEquals(GetAuthorResponseMother.getAuthorOkResponse(), response);
    verify(authorsRepository).findById(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("No author was found in database")
  void testRequestWhenNoAuthorFound() {
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    CoreException exception = assertThrowsExactly(CoreException.class,
      () -> authorsService.getAuthorById(UUID.randomUUID()));

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @ParameterizedTest
  @MethodSource("jpaExceptions")
  @DisplayName("Verify core exception when exceptions are thrown for findById")
  void verifyCoreExceptionWhenExceptionsAreThrownForFindById(Exception expectedException, CoreExceptionType exceptionType) {
    // Arrange
    when(authorsRepository.findById(any(UUID.class))).thenThrow(
       expectedException
    );

    // Act
    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.getAuthorById(UUID.randomUUID())
    );

    // Assert
    assertNotNull(exception);
    assertEquals(exceptionType, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Create author correctly verification")
  void verifyCreateAuthorCorrectly(){
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(false);
    when(authorsRepository.save(any(AuthorsEntity.class))).thenReturn(AuthorsEntityMother.authorsEntityWithoutBookOk());

    CreateAuthorResponse response = authorsService.createAuthor(CreateAuthorRequestMother.createAuthorRequestOk());

    assertNotNull(response);
    assertEquals(CreateAuthorResponseMother.createAuthorResponseOk(), response);
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verify(authorsRepository).existsByAuthorName(anyString());
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Verify create author when already exists")
  void verifyCreateAuthorWhenAlreadyExists(){
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(true);

    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.createAuthor(CreateAuthorRequestMother.createAuthorRequestOk())
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_ALREADY_EXISTS_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).existsByAuthorName(anyString());
    verifyNoMoreInteractions(authorsRepository);
  }
  @ParameterizedTest
  @MethodSource("jpaExceptions")
  @DisplayName("Verify core exception when saving new author")
  void verifyCoreExceptionWhenSavingAuthor(Exception expectedException, CoreExceptionType exceptionType) {
    // Arrange
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(false);
    when(authorsRepository.save(any(AuthorsEntity.class))).thenThrow(
       expectedException
    );

    // Act
    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.createAuthor(CreateAuthorRequestMother.createAuthorRequestOk())
    );

    // Assert
    assertNotNull(exception);
    assertEquals(exceptionType, exception.getExceptionType());
    verify(authorsRepository).existsByAuthorName(anyString());
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Correctly update author")
  void verifyUpdateAuthorCorrectly(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityOneOk()));
    when(authorsRepository.save(any(AuthorsEntity.class))).thenReturn(AuthorsEntityMother.authorEntityUpdatedOk());
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(false);

    UpdateAuthorResponse response = authorsService.updateAuthor(
       UpdateAuthorRequestMother.updateAuthorRequestOk(), UUID.fromString("ed67cda5-51e4-4047-a133-0942da7032c0")
    );

    assertNotNull(response);
    assertEquals(UpdateAuthorResponseMother.updateAuthorResponseOk(), response);
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verify(authorsRepository).existsByAuthorName(anyString());
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Verify when author id is not found in database")
  void verifyWhenAuthorIdIsNotFound(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    CoreException exception = assertThrowsExactly(CoreException.class,
      () -> authorsService.updateAuthor(
         UpdateAuthorRequestMother.updateAuthorRequestOk(), UUID.randomUUID()
      )
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Correctly update author when request name is null")
  void verifyWhenRequestNameIsNull(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.save(any(AuthorsEntity.class))).thenReturn(
       AuthorsEntityMother.authorEntityUpdatedWithSameNameOk());

    UpdateAuthorResponse response = authorsService.updateAuthor(
       UpdateAuthorRequestMother.updateAuthorRequestWithoutNameOk(), UUID.randomUUID()
    );

    assertNotNull(response);
    assertEquals(UpdateAuthorResponseMother.updateAuthorResponseWithoutUpdatedNameOk(), response);
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Correctly update author when request name is the same as found")
  void verifyWhenRequestNameIsTheSameAsFound(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.save(any(AuthorsEntity.class))).thenReturn(
       AuthorsEntityMother.authorEntityUpdatedWithSameNameOk());

    UpdateAuthorResponse response = authorsService.updateAuthor(
       UpdateAuthorRequestMother.updateAuthorRequestWithSameNameOk(), UUID.randomUUID()
    );

    assertNotNull(response);
    assertEquals(UpdateAuthorResponseMother.updateAuthorResponseWithoutUpdatedNameOk(), response);
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Verify when request name is not null and is different and exists in database")
  void verifyWhenRequestNameIsDifferentAndExists(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(true);

    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.updateAuthor(
          UpdateAuthorRequestMother.updateAuthorRequestOk(), UUID.randomUUID())
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_ALREADY_EXISTS_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).existsByAuthorName(anyString());
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Correctly update author when only name is provided")
  void verifyWhenOnlyNameIsProvided(){
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(false);
    when(authorsRepository.save(any(AuthorsEntity.class))).thenReturn(
       AuthorsEntityMother.authorsEntityWithOnlyUpdatedNameOk());

    UpdateAuthorResponse response = authorsService.updateAuthor(
       UpdateAuthorRequestMother.updateAuthorRequestOnlyNameOk(), UUID.randomUUID()
    );

    assertNotNull(response);
    assertEquals(UpdateAuthorResponseMother.updateAuthorResponseWithOnlyUpdatedNameOk(), response);
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @ParameterizedTest
  @MethodSource("jpaExceptions")
  @DisplayName("Verify core exception when updating author")
  void verifyCoreExceptionWhenUpdatingAuthor(Exception expectedException, CoreExceptionType exceptionType) {
    // Arrange
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.save(any(AuthorsEntity.class))).thenThrow(
       expectedException
    );
    when(authorsRepository.existsByAuthorName(anyString())).thenReturn(false);

    // Act
    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.updateAuthor(UpdateAuthorRequestMother.updateAuthorRequestOk(), UUID.randomUUID())
    );

    // Assert
    assertNotNull(exception);
    assertEquals(exceptionType, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).save(any(AuthorsEntity.class));
    verify(authorsRepository).existsByAuthorName(anyString());
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Correctly deletes an author")
  void verifyDeleteAuthor() {
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.hasBooksByAuthorId(any(UUID.class))).thenReturn(false);
    doNothing().when(authorsRepository).delete(any(AuthorsEntity.class));

    assertDoesNotThrow(() -> authorsService.deleteAuthorById(UUID.randomUUID()));

    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).hasBooksByAuthorId(any(UUID.class));
    verify(authorsRepository).delete(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Verify when no author was found for deletion")
  void verifyWhenNoAuthorWasFoundForDeletion() {
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.deleteAuthorById(UUID.randomUUID())
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @Test
  @DisplayName("Verify when author has active books")
  void verifyWhenAuthorHasActiveBooks() {
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.hasBooksByAuthorId(any(UUID.class))).thenReturn(true);

    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.deleteAuthorById(UUID.randomUUID())
    );

    assertNotNull(exception);
    assertEquals(CoreExceptionType.AUTHOR_HAS_BOOKS_EXCEPTION, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).hasBooksByAuthorId(any(UUID.class));
    verifyNoMoreInteractions(authorsRepository);
  }

  @ParameterizedTest
  @MethodSource("jpaExceptions")
  @DisplayName("Verify core exception when deleting author")
  void verifyCoreExceptionWhenDeletingAuthor(Exception expectedException, CoreExceptionType exceptionType) {
    // Arrange
    when(authorsRepository.findById(any(UUID.class))).thenReturn(Optional.of(AuthorsEntityMother.authorsEntityTwoOk()));
    when(authorsRepository.hasBooksByAuthorId(any(UUID.class))).thenReturn(false);
    doThrow(expectedException).when(authorsRepository).delete(any(AuthorsEntity.class));

    // Act
    CoreException exception = assertThrowsExactly(CoreException.class,
       () -> authorsService.deleteAuthorById(UUID.randomUUID())
    );

    // Assert
    assertNotNull(exception);
    assertEquals(exceptionType, exception.getExceptionType());
    verify(authorsRepository).findById(any(UUID.class));
    verify(authorsRepository).hasBooksByAuthorId(any(UUID.class));
    verify(authorsRepository).delete(any(AuthorsEntity.class));
    verifyNoMoreInteractions(authorsRepository);
  }
}
