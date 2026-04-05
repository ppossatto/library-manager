package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.ReservationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
