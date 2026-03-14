package com.ppossatto.librarymanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.Set;

@Table(name = "BOOKS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BooksEntity extends CommonEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookId;

  @Column(name = "TITLE", length = 200, nullable = false)
  private String bookTitle;

  @Column(name = "ISBN", length = 20, unique = true, nullable = false)
  private String bookIsbn;

  @ManyToMany
  @JoinTable(
     name = "BOOK_AUTHOR",
     joinColumns = @JoinColumn(name = "BOOK_ID"),
     inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID")
  )
  private Set<AuthorsEntity> authorsEntity;

  @Column(name = "PUBLISH_YEAR")
  private Year bookPublishYear;

  @Column(name = "EDITION", length = 50)
  private String bookEdition;

  @Column(name = "SYNOPSIS")
  private String bookSynopsis;

  @Column(name = "TOTAL_PAGES")
  private Integer bookTotalPages;
}
