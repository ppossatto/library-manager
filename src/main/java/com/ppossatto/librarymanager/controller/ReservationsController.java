package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;
import com.ppossatto.librarymanager.dto.request.CreateReservationRequest;
import com.ppossatto.librarymanager.dto.response.CreateReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetAllReservationsResponse;
import com.ppossatto.librarymanager.dto.response.GetReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetUserReservationsResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.dto.response.ReturnBookResponse;
import com.ppossatto.librarymanager.service.ReservationsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReservationsController {

  private final ReservationsService reservationsService;

  private static final String TRACE_ID = "traceId";

  @GetMapping
  public ResponseEntity<PageableResponse<GetAllReservationsResponse>> getAllReservations(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size,
     @RequestParam(required = false) ReservationStatus status,
     @RequestParam(required = false) UUID userId,
     @RequestParam(required = false) Long bookId

  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("""
       Get all reservations request...
       Page: {},
       Size: {},
       Reservation Status: {},
       User ID: {},
       Book ID: {}
       """, page, size,
       status != null ? status.getCode() : "-",
       userId != null ? userId : "-",
       bookId != null ? bookId : "-");
    Page<GetAllReservationsResponse> responsePage = reservationsService
       .getAllReservations(PageRequest.of(page, size), status, userId, bookId);
    return ResponseEntity.ok(PageableResponse.from(responsePage));
  }

  @GetMapping("my")
  public ResponseEntity<PageableResponse<GetUserReservationsResponse>> getUserReservations(
     @RequestParam(defaultValue = "0")
     @PositiveOrZero(message = "The page value cannot be negative")
     int page,
     @RequestParam(defaultValue = "3")
     @Positive(message = "The page size must be above zero")
     int size,
     @RequestParam(required = false) ReservationStatus status
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("""
       Get user reservations request...
       Page: {},
       Size: {},
       Reservation Status: {}
    """, page, size,
       status != null ? status.getCode() : "-");
    Page<GetUserReservationsResponse> responsePage = reservationsService
       .getUserReservations(PageRequest.of(page, size), status);
    return ResponseEntity.ok(PageableResponse.from(responsePage));
  }

  @GetMapping("{id}")
  public ResponseEntity<GetReservationResponse> getReservation(
     @PathVariable("id") Long reservationId
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("""
       Get reservation details request...
       Reservation ID: {},
    """, reservationId);
    return ResponseEntity.ok(reservationsService.getReservationById(reservationId));
  }

  @PostMapping
  public ResponseEntity<CreateReservationResponse> createReservation(
     @Valid @RequestBody CreateReservationRequest request
     ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Create reservation request...");
    CreateReservationResponse response = reservationsService.createReservation(request);
    return ResponseEntity.created(URI.create("/api/v1/reservations/" + response.id())).body(response);
  }

  @PatchMapping("{id}/return")
  public ResponseEntity<ReturnBookResponse> returnBook(
     @PathVariable("id") Long reservationId
  ){
    MDC.put(TRACE_ID, UUID.randomUUID().toString());
    log.info("Return book request for reservation ID: [{}]...", reservationId);
    return ResponseEntity.ok(reservationsService.returnBook(reservationId));
  }
}
