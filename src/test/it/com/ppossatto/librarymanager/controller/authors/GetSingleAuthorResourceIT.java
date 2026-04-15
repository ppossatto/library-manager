package com.ppossatto.librarymanager.controller.authors;

import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@TestPropertySource(properties = {
   "spring.flyway.enabled=false",
   "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Sql(
   executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
   scripts = {
      "classpath:scripts/populate_authors.sql",
      "classpath:scripts/populate_books.sql"
   }
)
@Sql(
   executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
   scripts = {
      "classpath:scripts/rollback_books.sql"
   }
)
class GetSingleAuthorResourceIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String AUTHOR_URL = "/api/v1/authors/%s";

  @Test
  @DisplayName("Get single author correctly")
  void getSingleAuthorCorrectly() {
    UUID authorId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    var response = restTemplate.exchange(
       String.format(AUTHOR_URL, authorId),
       HttpMethod.GET,
       null,
       GetAuthorResponse.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getHeaders().getContentType());
    assertTrue(response.getHeaders().getContentType().includes(org.springframework.http.MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("Verify error when no author is found")
  void exceptionWhenNoAuthorFound() {
    UUID authorId = UUID.fromString("aaaaaaaa-aaaa-aaaa-bbbb-aaaaaaaaaaaa");
    var response = restTemplate.exchange(
       String.format(AUTHOR_URL, authorId),
       HttpMethod.GET,
       null,
       Map.class
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getHeaders().getContentType());
    assertTrue(response.getHeaders().getContentType().includes(org.springframework.http.MediaType.APPLICATION_JSON));
    assertNotNull(response.getBody());
    assertEquals("ERR-61559", response.getBody().get("errorCode"));
    assertEquals("WARNING", response.getBody().get("severity"));
    assertEquals(String.format("Author with ID '%s' was not found", authorId), response.getBody().get("details"));
  }
}
