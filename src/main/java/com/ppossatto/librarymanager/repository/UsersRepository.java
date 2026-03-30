package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {

  @Query("SELECT u FROM UsersEntity u JOIN FETCH u.rolesEntity WHERE u.userEmail = :email")
  Optional<UsersEntity> findByUserEmailWithRoles(@Param("email") String email);

  boolean existsByUserEmail(String email);

  Optional<UsersEntity> findByUserId(UUID id);
}
