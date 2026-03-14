package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.domain.BookDto;
import com.ppossatto.librarymanager.dto.response.GetBookBasicResponse;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.mapper.BooksMapper;
import com.ppossatto.librarymanager.repository.BooksRepository;
import com.ppossatto.librarymanager.service.GetAllBooksService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllBooksServiceImpl implements GetAllBooksService {

  private final BooksRepository booksRepository;

  @Override
  public Page<GetBookBasicResponse> getAllBooks(Pageable pageable, UUID traceId) {
    try {
      log.debug("""
            Getting all books service implementation...
            Trace ID: {}
            """,
         traceId);

      Page<BookDto> dbResponse =
         booksRepository
            .getAll(pageable)
            .map(BooksMapper::toDto);

      log.debug("""
            Returned books from database...
            Size: {}
            Trace ID: {}
            """,
         dbResponse.getContent().size(), traceId);

      return dbResponse.map(BooksMapper::toBasicResponse);
    } catch (QueryTimeoutException e) {
      log.error("""
         Timeout error while running JPA getAll query.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.GET_ALL_BOOKS_JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("""
         JPA error while getting all books data.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.GET_ALL_BOOKS_JPA_EXCEPTION, e);
    } catch (Exception e) {
      log.error("""
         Unexpected exception while getting all books.
         Trace ID: {}
         """, traceId);
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
