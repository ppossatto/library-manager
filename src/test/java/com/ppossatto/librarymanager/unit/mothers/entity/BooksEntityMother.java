package com.ppossatto.librarymanager.unit.mothers.entity;

import com.ppossatto.librarymanager.entity.BooksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Year;
import java.util.HashSet;
import java.util.List;

public class BooksEntityMother {

  public static BooksEntity booksEntityOneOk() {
    BooksEntity book = new BooksEntity();
    book.setBookId(1L);
    book.setBookTitle("Lord of the Rings");
    book.setBookIsbn("9780261102439");
    book.setBookPublishYear(Year.of(1992));
    book.setBookEdition("HarperCollins");
    book.setBookSynopsis("Continuing the story of The Hobbit");
    book.setBookTotalPages(1198);
    book.setAuthorsEntity(new HashSet<>());
    return book;
  }

  public static BooksEntity booksEntityTwoOk() {
    BooksEntity book = new BooksEntity();
    book.setBookId(2L);
    book.setBookTitle("Harry Potter and the Sorcerer's Stone");
    book.setBookIsbn("9780590353427");
    book.setBookPublishYear(Year.of(1998));
    book.setBookEdition("Scholastic");
    book.setBookSynopsis("Harry Potter has never been the star of a Quidditch team");
    book.setBookTotalPages(320);
    book.setAuthorsEntity(new HashSet<>());
    return book;
  }

  public static Page<BooksEntity> booksEntityPageOk() {
    List<BooksEntity> books = List.of(booksEntityOneOk(), booksEntityTwoOk());
    Pageable pageable = PageRequest.of(0, 3);
    return new PageImpl<>(books, pageable, books.size());
  }

  public static Page<BooksEntity> booksEntityPageEmpty() {
    return new PageImpl<>(List.of(), PageRequest.of(0, 3), 0);
  }
}
