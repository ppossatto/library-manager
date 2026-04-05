package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.AuthorsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorsRepository extends JpaRepository<AuthorsEntity, UUID> {

  Optional<AuthorsEntity> findById(UUID id);

  @Query("""
    SELECT a FROM AuthorsEntity a
        WHERE (:name IS NULL OR a.authorName = :name)
    """)
  Page<AuthorsEntity> findAuthorsWithFilter(
     @Param("name") String name,
     Pageable pageable
  );

  boolean existsByAuthorName(String name);

  @Query("""
    SELECT COUNT(a) > 0 FROM AuthorsEntity a
    JOIN a.booksEntity b
    WHERE a.authorId = :authorId
    """)
  boolean hasBooksByAuthorId(@Param("authorId") UUID authorId);
}
