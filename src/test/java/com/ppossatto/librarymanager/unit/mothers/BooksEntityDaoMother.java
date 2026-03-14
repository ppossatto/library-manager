package com.ppossatto.librarymanager.unit.mothers;

import com.ppossatto.librarymanager.entity.AuthorsEntity;
import com.ppossatto.librarymanager.entity.BooksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BooksEntityDaoMother {

  public static Page<BooksEntity> booksPageResponseOk(){
    List<BooksEntity> responseList = threeElementsOk();
    Pageable pageable = PageRequest.of(0, 10);
    return new PageImpl<>(responseList, pageable, responseList.size());
  }

  public static List<BooksEntity> threeElementsOk(){
    return List.of(
       getBookEntity1(),
       getBookEntity2(),
       getBookEntity3()
    );
  }

  public static BooksEntity getBookEntity3() {
    return new BooksEntity(
       3L,
       "Design Patterns",
       "978-0201633610",
       Set.of(
          new AuthorsEntity(
             UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012"),
             "Erich Gamma",
             null,
             LocalDate.of(1961, 3, 13),
             "Swiss",
             "Computer scientist and co-author of Design Patterns."
          ),
          new AuthorsEntity(
             UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123"),
             "Richard Helm",
             null,
             LocalDate.of(1962, 1, 1),
             "Australian",
             "Software engineer and co-author of Design Patterns."
          )
       ),
       Year.of(1994),
       "1st Edition",
       "Elements of reusable object-oriented software.",
       395
    );
  }

  public static BooksEntity getBookEntity2() {
    return new BooksEntity(
       2L,
       "Effective Java",
       "978-0134685991",
       Set.of(new AuthorsEntity(
          UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
          "Joshua Bloch",
          null,
          LocalDate.of(1961, 8, 28),
          "American",
          "Software engineer, worked at Sun Microsystems and Google."
       )),
       Year.of(2018),
       "3rd Edition",
       "Best practices for the Java programming language.",
       412
    );
  }

  public static BooksEntity getBookEntity1() {
    return new BooksEntity(
       1L,
       "Clean Code",
       "978-0132350884",
       Set.of(new AuthorsEntity(
          UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
          "Robert C. Martin",
          null,
          LocalDate.of(1952, 12, 5),
          "American",
          "Software engineer and author, known as Uncle Bob."
       )),
       Year.of(2008),
       "1st Edition",
       "A handbook of agile software craftsmanship.",
       431
    );
  }
}
