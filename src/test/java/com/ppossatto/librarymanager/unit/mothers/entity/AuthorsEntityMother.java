package com.ppossatto.librarymanager.unit.mothers.entity;

import com.ppossatto.librarymanager.entity.AuthorsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AuthorsEntityMother {

  public static Page<AuthorsEntity> authorsEntityPageOk() {
    List<AuthorsEntity> authors = List.of(authorsEntityOneOk(), authorsEntityTwoOk());
    Pageable pageable = PageRequest.of(0, 3);

    return new PageImpl<>(authors, pageable, authors.size());
  }

  public static AuthorsEntity authorsEntityOneOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("fb1c26ad-a614-49b9-916e-c49aeeef28b7"));
    author.setAuthorName("J. R. R. Tolkien");
    author.setAuthorBirthDate(LocalDate.of(1892, 1, 3));
    author.setAuthorNationality("South African");
    author.setAuthorBiography("Biography of Tolkien");
    author.setBooksEntity(Set.of(BooksEntityMother.booksEntityOneOk()));
    return author;
  }

  public static AuthorsEntity authorsEntityTwoOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("4be84d9a-dff1-4e6a-81e5-cd824029d5ee"));
    author.setAuthorName("J. K. Rowling");
    author.setAuthorBirthDate(LocalDate.of(1965, 7, 31));
    author.setAuthorNationality("British");
    author.setAuthorBiography("Biography of Rowling");
    author.setBooksEntity(Set.of(BooksEntityMother.booksEntityTwoOk()));
    return author;
  }

  public static AuthorsEntity authorsEntityWithoutBookOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("4b0caf56-dc3d-421d-a9a3-2611684a1608"));
    author.setAuthorName("Stephen King");
    author.setAuthorBirthDate(LocalDate.of(1947, 9, 21));
    author.setAuthorNationality("American");
    author.setAuthorBiography("Biography of Stephen King");
    return author;
  }

  public static AuthorsEntity authorEntityUpdatedOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("ed67cda5-51e4-4047-a133-0942da7032c0"));
    author.setAuthorName("John");
    author.setAuthorBirthDate(LocalDate.of(1980, 1, 1));
    author.setAuthorNationality("American");
    author.setAuthorBiography("Updated biography");
    return author;
  }

  public static AuthorsEntity authorEntityUpdatedWithSameNameOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("ed67cda5-51e4-4047-a133-0942da7032c0"));
    author.setAuthorName("J. K. Rowling");
    author.setAuthorBirthDate(LocalDate.of(1980, 1, 1));
    author.setAuthorNationality("American");
    author.setAuthorBiography("Updated biography");
    return author;
  }

  public static AuthorsEntity authorsEntityWithOnlyUpdatedNameOk(){
    AuthorsEntity author = new AuthorsEntity();
    author.setAuthorId(UUID.fromString("4be84d9a-dff1-4e6a-81e5-cd824029d5ee"));
    author.setAuthorName("John");
    author.setAuthorBirthDate(LocalDate.of(1965, 7, 31));
    author.setAuthorNationality("British");
    author.setAuthorBiography("Biography of Rowling");
    author.setBooksEntity(Set.of(BooksEntityMother.booksEntityTwoOk()));
    return author;
  }

  public static Page<AuthorsEntity> authorsEntityPageEmpty() {
    return new PageImpl<>(List.of(), PageRequest.of(0, 3), 0);
  }
}
