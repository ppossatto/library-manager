package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.ReservationsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Long> {

  @Query("""
     SELECT COUNT(r) > 0 FROM ReservationsEntity r
         WHERE r.usersEntity.userId = :userId
             AND r.reservationStatus IN ('active', 'overdue')
     """)
  boolean hasActiveOrOverdueReservations(@Param("userId") UUID userId);

  @Query("""
     SELECT COUNT(r) > 0 FROM ReservationsEntity r
         WHERE r.booksEntity.bookId = :bookId
             AND r.reservationStatus IN ('active', 'overdue')
     """)
  boolean hasActiveOrOverdueReservationsByBookId(@Param("bookId") Long bookId);

  @Query("""
    SELECT r FROM ReservationsEntity r
        WHERE (:status IS NULL OR r.reservationStatus = :status)
        AND (:userId IS NULL OR r.usersEntity.userId = :userId)
        AND (:bookId IS NULL OR r.booksEntity.bookId = :bookId)
    """)
  Page<ReservationsEntity> findReservationsWithFilters(
     Pageable pageable,
     @Param("status") String reservationStatus,
     @Param("userId") UUID userId,
     @Param("bookId") Long bookId
  );

  @Query("""
    SELECT r FROM ReservationsEntity r
        WHERE r.usersEntity.userEmail = :userEmail
        AND (:status IS NULL OR r.reservationStatus = :status)
    """)
  Page<ReservationsEntity> findUserReservationsWithFilters(
     Pageable pageable,
     @Param("userEmail") String userEmail,
     @Param("status") String status
  );

  @Query("""
    SELECT r FROM ReservationsEntity r
        WHERE (:userEmail IS NULL OR r.usersEntity.userEmail = :userEmail)
        AND r.reservationId = :reservationId
    """)
  Optional<ReservationsEntity> findReservationWithFilters(
     @Param("userEmail") String userEmail,
     @Param("reservationId") Long reservationId
  );
}
