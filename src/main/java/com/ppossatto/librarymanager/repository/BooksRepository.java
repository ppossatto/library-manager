package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.BooksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends JpaRepository<BooksEntity, Long> {

  @Query("SELECT b FROM BooksEntity b")
  Page<BooksEntity> getAll(Pageable pageable);
}
