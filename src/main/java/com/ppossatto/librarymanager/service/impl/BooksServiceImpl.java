package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.response.GetBookBasicAuthorDataResponse;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.BooksRepository;
import com.ppossatto.librarymanager.service.BooksService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BooksServiceImpl implements BooksService {

  private final BooksRepository booksRepository;

  @Override
  public Page<GetBookBasicResponse> getAllBooks(Pageable pageable, UUID traceId) {
    try {
      log.debug("""
            Getting all books service implementation...
            Trace ID: {}
            """,
         traceId);

      return booksRepository
         .getAll(pageable)
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
      log.error("""
         Timeout error while running JPA getAll query.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("""
         JPA error while getting all books data.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("""
         Unexpected exception while getting all books.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
