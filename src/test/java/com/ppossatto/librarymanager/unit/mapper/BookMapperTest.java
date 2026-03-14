package com.ppossatto.librarymanager.unit.mapper;

import com.ppossatto.librarymanager.dto.domain.BookDto;
import com.ppossatto.librarymanager.mapper.BooksMapper;
import com.ppossatto.librarymanager.unit.mothers.BookDtoMother;
import com.ppossatto.librarymanager.unit.mothers.BooksEntityDaoMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class BookMapperTest {

  @Test
  @DisplayName("""
     GIVEN an entity object
     WHEN toDto mapper method is called
     THEN validate the correct mapped object
     """)
  void testToDtoMapperMethodOk(){
    // Arrange && Act
    BookDto dto = BooksMapper.toDto(BooksEntityDaoMother.getBookEntity1());

    // Assert
    assertNotNull(dto);
    assertEquals(BookDtoMother.getBookDto1(), dto);
  }

  @Test
  @DisplayName("""
     GIVEN a null parameter
     WHEN toDto mapper method is called
     THEN validate it returns null
     """)
  void testToDtoMapperMethodNull(){
    // Arrange & Act & Assert
    assertNull(BooksMapper.toDto(null));
  }

  @Test
  @DisplayName("""
     GIVEN an entity object
     AND the book object has more than one author object
     WHEN toDto mapper method is called
     THEN validate the correct mapped object
     """)
  void testToDtoMapperMethodWithTwoAuthorsOk(){
    // Arrange && Act
    BookDto dto = BooksMapper.toDto(BooksEntityDaoMother.getBookEntity3());

    // Assert
    assertNotNull(dto);
    assertEquals(BookDtoMother.getBookDto3(), dto);
  }
}
