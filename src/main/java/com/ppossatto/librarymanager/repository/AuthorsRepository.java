package com.ppossatto.librarymanager.repository;

import com.ppossatto.librarymanager.entity.AuthorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorsRepository extends JpaRepository<AuthorsEntity, UUID> {
}
