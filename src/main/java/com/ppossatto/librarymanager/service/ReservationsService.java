package com.ppossatto.librarymanager.service;

import com.ppossatto.librarymanager.dto.domain.enums.ReservationStatus;
import com.ppossatto.librarymanager.dto.request.CreateReservationRequest;
import com.ppossatto.librarymanager.dto.response.CreateReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetAllReservationsResponse;
import com.ppossatto.librarymanager.dto.response.GetReservationResponse;
import com.ppossatto.librarymanager.dto.response.GetUserReservationsResponse;
import com.ppossatto.librarymanager.dto.response.ReturnBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReservationsService {

  Page<GetAllReservationsResponse> getAllReservations(
     Pageable pageable,
     ReservationStatus status,
     UUID userId,
     Long bookId);

  Page<GetUserReservationsResponse> getUserReservations(
     Pageable pageable,
     ReservationStatus status
  );

  GetReservationResponse getReservationById(Long reservationId);

  CreateReservationResponse createReservation(CreateReservationRequest request);

  ReturnBookResponse returnBook(Long reservationId);
}
