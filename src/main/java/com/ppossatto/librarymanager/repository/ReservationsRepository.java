package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.ReservationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Long> {
}
