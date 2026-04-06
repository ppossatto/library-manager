package com.ppossatto.librarymanager.service.impl;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;
import com.ppossatto.librarymanager.dto.domain.enums.UserStatus;
import com.ppossatto.librarymanager.dto.request.CreateReservationRequest;
import com.ppossatto.librarymanager.dto.response.CreateReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetAllReservationsResponse;
import com.ppossatto.librarymanager.dto.response.GetBookIdAndTitle;
import com.ppossatto.librarymanager.dto.response.GetReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetUserIdAndName;
import com.ppossatto.librarymanager.dto.response.GetUserReservationsResponse;
import com.ppossatto.librarymanager.dto.response.ReturnBookResponse;
import com.ppossatto.librarymanager.entity.BooksEntity;
import com.ppossatto.librarymanager.entity.ReservationsEntity;
import com.ppossatto.librarymanager.entity.UsersEntity;
import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.repository.BooksRepository;
import com.ppossatto.librarymanager.repository.ReservationsRepository;
import com.ppossatto.librarymanager.repository.UsersRepository;
import com.ppossatto.librarymanager.security.utils.SecurityUtils;
import com.ppossatto.librarymanager.service.ReservationsService;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationsServiceImpl implements ReservationsService {

  private final ReservationsRepository reservationsRepository;
  private final SecurityUtils securityUtils;
  private final UsersRepository usersRepository;
  private final BooksRepository booksRepository;

  @Value("${reservation.period}")
  private Integer reservationPeriod;

  @Override
  @Transactional(readOnly = true)
  public Page<GetAllReservationsResponse> getAllReservations(Pageable pageable,
                                                             ReservationStatus status,
                                                             UUID userId,
                                                             Long bookId) {
    log.debug("Get all reservations service implementation...");
    try{
      return reservationsRepository.findReservationsWithFilters(
         pageable,
         status != null ? status.getCode(): null,
         userId,
         bookId
      ).map(
         reservation -> GetAllReservationsResponse.builder()
            .id(reservation.getReservationId())
            .user(GetUserIdAndName.builder()
               .id(reservation.getUsersEntity().getUserId())
               .name(reservation.getUsersEntity().getUserName())
               .build())
            .book(GetBookIdAndTitle.builder()
               .id(reservation.getBooksEntity().getBookId())
               .title(reservation.getBooksEntity().getBookTitle())
               .build())
            .status(reservation.getReservationStatus())
            .build()
      );
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findReservationsWithFilters query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting all reservations data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting all reservations.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GetUserReservationsResponse> getUserReservations(Pageable pageable, ReservationStatus status) {
    log.debug("Get user reservations service implementation...");
    try{
      String authenticatedEmail = securityUtils.getAuthenticatedEmail();
      return reservationsRepository.findUserReservationsWithFilters(pageable, authenticatedEmail,
            status != null ? status.getCode() : null)
         .map(reservation -> GetUserReservationsResponse.builder()
            .id(reservation.getReservationId())
            .book(GetBookIdAndTitle.builder()
               .id(reservation.getBooksEntity().getBookId())
               .title(reservation.getBooksEntity().getBookTitle())
               .build())
            .status(reservation.getReservationStatus())
            .build());
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA findUserReservationsWithFilters query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting user reservations data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting user reservations.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetReservationResponse getReservationById(Long reservationId) {
    log.debug("Get reservation service implementation for Reservation with id [{}]...", reservationId);
    try{
      String email = securityUtils.getAuthenticatedEmail();
      if(securityUtils.isLibrarian()){
        email = null;
      }
      return reservationsRepository.findReservationWithFilters(email, reservationId).map(
         reservation -> GetReservationResponse.builder()
            .id(reservation.getReservationId())
            .book(GetBookIdAndTitle.builder()
               .id(reservation.getBooksEntity().getBookId())
               .title(reservation.getBooksEntity().getBookTitle())
               .build())
            .user(GetUserIdAndName.builder()
               .id(reservation.getUsersEntity().getUserId())
               .name(reservation.getUsersEntity().getUserName())
               .build())
            .reservationDate(reservation.getReservationDate())
            .expectedDevolutionDate(reservation.getReservationExpectedDevolutionDate())
            .devolutionDate(reservation.getReservationDevolutionDate())
            .status(reservation.getReservationStatus())
            .observations(reservation.getReservationObservations())
            .build()
      ).orElseThrow(
         () -> new CoreException(CoreExceptionType.RESERVATION_NOT_FOUND_EXCEPTION, reservationId.toString())
      );
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA find query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while getting reservation data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while getting reservation.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public CreateReservationResponse createReservation(CreateReservationRequest request) {
    log.debug("Create reservation service request...");
    try{
      UsersEntity userFound = usersRepository.findByUserId(request.userId()).orElseThrow(
         () -> new CoreException(CoreExceptionType.USER_ID_NOT_FOUND_EXCEPTION, request.userId().toString())
      );
      if(userFound.getUserStatus().equals(UserStatus.INACTIVE.getCode())){
        throw new CoreException(CoreExceptionType.INACTIVE_USER_EXCEPTION);
      }
      if(userFound.getUserStatus().equals(UserStatus.BLOCKED.getCode())){
        throw new CoreException(CoreExceptionType.BLOCKED_USER_EXCEPTION);
      }
      BooksEntity bookFound = booksRepository.findByBookId(request.bookId()).orElseThrow(
         () -> new CoreException(CoreExceptionType.BOOK_NOT_FOUND_EXCEPTION, request.bookId().toString())
      );
      ReservationsEntity reservationToSave = new ReservationsEntity();
      reservationToSave.setBooksEntity(bookFound);
      reservationToSave.setUsersEntity(userFound);
      reservationToSave.setReservationExpectedDevolutionDate(LocalDate.now().plusDays(reservationPeriod));
      reservationToSave.setReservationObservations(request.observations());
      reservationToSave.setReservationStatus(ReservationStatus.ACTIVE.getCode());
      ReservationsEntity savedReservation = reservationsRepository.save(reservationToSave);
      return CreateReservationResponse.builder()
         .id(savedReservation.getReservationId())
         .book(GetBookIdAndTitle.builder()
            .id(savedReservation.getBooksEntity().getBookId())
            .title(savedReservation.getBooksEntity().getBookTitle())
            .build())
         .user(GetUserIdAndName.builder()
            .id(savedReservation.getUsersEntity().getUserId())
            .name(savedReservation.getUsersEntity().getUserName())
            .build())
         .expectedDevolutionDate(savedReservation.getReservationExpectedDevolutionDate())
         .observations(savedReservation.getReservationObservations())
         .build();
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA save query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while saving reservation data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while saving reservation.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @Override
  @Transactional
  public ReturnBookResponse returnBook(Long reservationId) {
    try{
      ReservationsEntity foundReservation = reservationsRepository.findById(reservationId).orElseThrow(
         () -> new CoreException(CoreExceptionType.RESERVATION_NOT_FOUND_EXCEPTION, reservationId.toString())
      );
      if(foundReservation.getReservationStatus().equals(ReservationStatus.RETURNED.getCode())){
        throw new CoreException(CoreExceptionType.RESERVATION_ALREADY_RETURNED_EXCEPTION, reservationId.toString());
      }
      foundReservation.setReservationStatus(ReservationStatus.RETURNED.getCode());
      foundReservation.setReservationDevolutionDate(LocalDate.now());
      ReservationsEntity updatedReservation = reservationsRepository.save(foundReservation);
      boolean returnedLate = updatedReservation.getReservationDevolutionDate()
         .isAfter(updatedReservation.getReservationExpectedDevolutionDate());

      if(returnedLate){
        log.info("Returned book for reservation ID: [{}] was returned late", reservationId);
      }

      return ReturnBookResponse.builder()
         .reservationId(updatedReservation.getReservationId())
         .expectedDevolutionDate(updatedReservation.getReservationExpectedDevolutionDate())
         .devolutionDate(updatedReservation.getReservationDevolutionDate())
         .returnedLate(returnedLate)
         .build();
    } catch (CoreException ce){
      throw ce;
    } catch (QueryTimeoutException e) {
      log.error("Timeout error while running JPA update query.");
      throw new CoreException(CoreExceptionType.JPA_TIMEOUT_EXCEPTION, e);
    } catch (PersistenceException e) {
      log.error("JPA error while updating reservation data.");
      throw new CoreException(CoreExceptionType.JPA_PERSISTENCE_EXCEPTION, e);
    } catch (Exception e) {
      log.error("Unexpected exception while updating reservation.");
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }
}
