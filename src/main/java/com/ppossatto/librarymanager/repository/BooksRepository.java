package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.BooksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<BooksEntity, Long> {

  @Query("""
     SELECT b FROM BooksEntity b
     WHERE (:title IS NULL OR b.bookTitle = :title)
     AND (:isbn IS NULL OR b.bookIsbn = :isbn)
     """)
  Page<BooksEntity> findAllWithFilters(
     @Param("title") String title,
     @Param("isbn") String isbn,
     Pageable pageable
  );

  Optional<BooksEntity> findByBookId(Long bookId);

  boolean existsByBookIsbn(String isbn);
}
