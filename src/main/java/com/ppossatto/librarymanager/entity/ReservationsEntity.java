package com.ppossatto.librarymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Table(name = "RESERVATIONS")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsEntity extends CommonEntity {

  @Getter
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reservationId;

  @ManyToOne
  @JoinColumn(name = "BOOK_ID", nullable = false)
  private BooksEntity booksEntity;

  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private UsersEntity usersEntity;

  @Column(name = "RESERVATION_DATE", updatable = false, insertable = false)
  private LocalDate reservationDate;

  @Column(name = "EXPECTED_DEVOLUTION_DATE", updatable = false)
  private LocalDate reservationExpectedDevolutionDate;

  @Column(name = "DEVOLUTION_DATE")
  private LocalDate reservationDevolutionDate;

  @Column(name = "STATUS", length = 20)
  private String reservationStatus;

  @Column(name = "OBSERVATIONS")
  private String reservationObservations;
}
