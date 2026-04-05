package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.request.CreateBookRequest;
import com.ppossatto.librarymanager.dto.request.UpdateBookRequest;
import com.ppossatto.librarymanager.dto.response.AddAuthorToBookResponse;
import com.ppossatto.librarymanager.dto.response.CreateBookResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorNameAndUuid;
import com.ppossatto.librarymanager.dto.response.GetBookBasicAuthorDataResponse;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.dto.response.GetBookResponse;
import com.ppossatto.librarymanager.dto.response.UpdateBookResponse;
import com.ppossatto.librarymanager.entity.AuthorsEntity;
import com.ppossatto.librarymanager.entity.BooksEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.AuthorsRepository;
import com.ppossatto.librarymanager.repository.BooksRepository;
import com.ppossatto.librarymanager.repository.ReservationsRepository;
import com.ppossatto.librarymanager.service.BooksService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BooksServiceImpl implements BooksService {

  private final BooksRepository booksRepository;
  private final AuthorsRepository authorsRepository;
  private final ReservationsRepository reservationsRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<GetBookBasicResponse> getAllBooks(Pageable pageable, String title, String isbn) {
    log.debug("Getting all books service implementation");
    try {
      return booksRepository
         .findAllWithFilters(title, isbn, pageable)
         .map(book -> GetBookBasicResponse.builder()
            .id(book.getBookId())
            .title(book.getBookTitle())
            .edition(book.getBookEdition())
            .publishedYear(book.getBookPublishYear())
            .authors(book.getAuthorsEntity().stream()
               .map(
                  author -> GetBookBasicAuthorDataResponse.builder()
                     .id(author.getAuthorId())
                     .name(author.getAuthorName())
                     .build()
               )
               .collect(Collectors.toSet()))
            .build());
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findAllWithFilters query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting all books data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting all books.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetBookResponse getBookById(Long bookId) {
    log.debug("Getting book with id [{}] service implementation", bookId);
    try {
      return booksRepository.findByBookId(bookId)
         .map(book -> GetBookResponse.builder()
            .id(book.getBookId())
            .title(book.getBookTitle())
            .isbn(book.getBookIsbn())
            .authors(book.getAuthorsEntity().stream().map(AuthorsEntity::getAuthorName).collect(Collectors.toSet()))
            .publishYear(book.getBookPublishYear())
            .edition(book.getBookEdition())
            .synopsis(book.getBookSynopsis())
            .totalPages(book.getBookTotalPages())
            .build())
         .orElseThrow(() -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, bookId.toString()));
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findById query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting book data with defined ID.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting book with specified ID.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public CreateBookResponse createBook(CreateBookRequest request) {
    log.debug("Creating a new book service implementation");
    try {
      verifyIfPublishYearIsBeforeOrEqualsCurrentYear(request.publishYear());
      if (booksRepository.existsByBookIsbn(request.isbn())) {
        throw new CoreException(CoreExceptionType.ISBN_ALREADY_EXISTS);
      }
      Set<AuthorsEntity> authorsFound = new HashSet<>();
      request.authors().forEach(authorUuid -> authorsFound.add(
         authorsRepository.findById(authorUuid).orElseThrow(
            () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, authorUuid.toString())
         ))
      );
      BooksEntity bookToSave = new BooksEntity();
      bookToSave.setBookTitle(request.title());
      bookToSave.setBookIsbn(request.isbn());
      bookToSave.setAuthorsEntity(authorsFound);
      bookToSave.setBookPublishYear(request.publishYear());
      bookToSave.setBookEdition(request.edition());
      bookToSave.setBookSynopsis(request.synopsis());
      bookToSave.setBookTotalPages(request.totalPages());

      BooksEntity savedBook = booksRepository.save(bookToSave);
      return CreateBookResponse.builder()
         .id(savedBook.getBookId())
         .title(savedBook.getBookTitle())
         .isbn(savedBook.getBookIsbn())
         .authors(
            savedBook.getAuthorsEntity().stream().map(
               authorsEntity -> GetAuthorNameAndUuid.builder()
                  .authorId(authorsEntity.getAuthorId())
                  .authorName(authorsEntity.getAuthorName())
                  .build()
            ).collect(Collectors.toSet())
         )
         .publishYear(savedBook.getBookPublishYear())
         .edition(savedBook.getBookEdition())
         .synopsis(savedBook.getBookSynopsis())
         .totalPages(savedBook.getBookTotalPages())
         .build();
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA save query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while saving book data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while saving book.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  private static void verifyIfPublishYearIsBeforeOrEqualsCurrentYear(Year yearToValidate) {
    if (yearToValidate != null && yearToValidate.isAfter(Year.now())) {
      throw new CoreException(CoreExceptionType.INVALID_PUBLISH_YEAR_EXCEPTION);
    }
  }

  @Override
  @Transactional
  public UpdateBookResponse updateBook(UpdateBookRequest request, Long bookId) {
    log.debug("Updating book with id [{}] service implementation", bookId);
    try {
      verifyIfPublishYearIsBeforeOrEqualsCurrentYear(request.publishYear());
      BooksEntity bookToUpdate = booksRepository.findById(bookId).orElseThrow(
         () -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, bookId.toString())
      );
      if (!bookToUpdate.getBookIsbn().equals(request.isbn()) &&
         booksRepository.existsByBookIsbn(request.isbn())) {
        throw new CoreException(CoreExceptionType.ISBN_ALREADY_EXISTS);
      }
      bookToUpdate.setBookTitle(request.title());
      bookToUpdate.setBookIsbn(request.isbn());
      if (request.publishYear() != null) {
        bookToUpdate.setBookPublishYear(request.publishYear());
      }
      if (request.edition() != null) {
        bookToUpdate.setBookEdition(request.edition());
      }
      if (request.synopsis() != null) {
        bookToUpdate.setBookSynopsis(request.synopsis());
      }
      if (request.totalPages() != null) {
        bookToUpdate.setBookTotalPages(request.totalPages());
      }
      BooksEntity savedBook = booksRepository.save(bookToUpdate);
      return UpdateBookResponse.builder()
         .id(savedBook.getBookId())
         .title(savedBook.getBookTitle())
         .isbn(savedBook.getBookIsbn())
         .publishYear(savedBook.getBookPublishYear())
         .edition(savedBook.getBookEdition())
         .synopsis(savedBook.getBookSynopsis())
         .totalPages(savedBook.getBookTotalPages())
         .build();
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA update query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while updating book data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while updating book.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void deleteBookById(Long bookId) {
    log.debug("Deleting book with id [{}] service implementation", bookId);
    try {
      booksRepository.findById(bookId).orElseThrow(
         () -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, bookId.toString())
      );
      if (reservationsRepository.hasActiveOrOverdueReservationsByBookId(bookId)) {
        throw new CoreException(CoreExceptionType.BOOK_HAS_ACTIVE_RESERVATIONS_EXCEPTION);
      }
      booksRepository.deleteById(bookId);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA delete query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while deleting book data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while deleting book.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public AddAuthorToBookResponse addAuthorToBook(Long bookId, UUID authorId) {
    log.debug("Adding author with ID [{}] for book with ID [{}] service implementation...", authorId, bookId);
    try {
      AuthorsEntity authorFound = authorsRepository.findById(authorId).orElseThrow(
         () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, authorId.toString())
      );
      BooksEntity bookFound = booksRepository.findById(bookId).orElseThrow(
         () -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, bookId.toString())
      );
      if (bookFound.getAuthorsEntity().contains(authorFound)) {
        throw new CoreException(CoreExceptionType.AUTHOR_ALREADY_IN_BOOK_EXCEPTION, authorId.toString());
      }

      Set<AuthorsEntity> authorsForBook = bookFound.getAuthorsEntity();
      authorsForBook.add(authorFound);

      BooksEntity savedBook = booksRepository.save(bookFound);

      return AddAuthorToBookResponse.builder()
         .id(savedBook.getBookId())
         .title(savedBook.getBookTitle())
         .isbn(savedBook.getBookIsbn())
         .authors(savedBook.getAuthorsEntity().stream().map(
               author ->
                  GetAuthorNameAndUuid.builder()
                     .authorId(author.getAuthorId())
                     .authorName(author.getAuthorName())
                     .build()
            )
            .collect(Collectors.toSet()))
         .publishYear(savedBook.getBookPublishYear())
         .edition(savedBook.getBookEdition())
         .synopsis(savedBook.getBookSynopsis())
         .totalPages(savedBook.getBookTotalPages())
         .build();
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA insert book authors query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while adding book authors data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while adding book authors.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public void deleteAuthorFromBook(Long bookId, UUID authorId) {
    log.debug("Deleting author with ID [{}] for book with ID [{}] service implementation...", authorId, bookId);
    try {
      AuthorsEntity authorFound = authorsRepository.findById(authorId).orElseThrow(
         () -> new CoreException(CoreExceptionType.AUTHOR_NOT_FOUND_EXCEPTION, authorId.toString())
      );
      BooksEntity bookFound = booksRepository.findById(bookId).orElseThrow(
         () -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, bookId.toString())
      );
      if (bookFound.getAuthorsEntity().size() == 1) {
        throw new CoreException(CoreExceptionType.BOOK_MUST_HAVE_AT_LEAST_ONE_AUTHOR_EXCEPTION);
      }
      if (!bookFound.getAuthorsEntity().contains(authorFound)) {
        throw new CoreException(CoreExceptionType.AUTHOR_NOT_IN_BOOK_EXCEPTION, authorId.toString());
      }
      Set<AuthorsEntity> authorsForBook = bookFound.getAuthorsEntity();
      authorsForBook.remove(authorFound);
      booksRepository.save(bookFound);
    } catch (CoreException ce) {
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA deleting book author query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while deleting book author data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while deleting book author.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
