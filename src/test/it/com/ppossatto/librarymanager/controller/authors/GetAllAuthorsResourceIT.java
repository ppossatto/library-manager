package com.ppossatto.librarymanager.controller.authors;

import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
   scripts = "classpath:scripts/populate_authors.sql"
)
@Sql(
   executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
   scripts = "classpath:scripts/rollback_authors.sql"
)
class GetAllAuthorsResourceIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String AUTHORS_URL = "/api/v1/authors";

  @Test
  @DisplayName("Test GET authors when has elements in the database")
  void testGetAuthorsWhenHasElementsInDatabase(){
    String queryParams = "?page=0&size=10";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertFalse(responseBody.content().isEmpty());
    assertEquals(10, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(2, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with default pagination values")
  void testGetAuthorsWithDefaultPagination() {
    var response = restTemplate.exchange(
       AUTHORS_URL,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertFalse(responseBody.content().isEmpty());
    assertEquals(3, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(5, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with custom page and size")
  void testGetAuthorsWithCustomPagination() {
    String queryParams = "?page=1&size=5";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(5, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(3, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with large page size")
  void testGetAuthorsWithLargePageSize() {
    String queryParams = "?page=0&size=20";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(15, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(1, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with last page")
  void testGetAuthorsWithLastPage() {
    String queryParams = "?page=2&size=5";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(5, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(3, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with page beyond total pages")
  void testGetAuthorsWithPageBeyondTotalPages() {
    String queryParams = "?page=10&size=5";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertTrue(responseBody.content().isEmpty());
    assertEquals(15, responseBody.totalElements());
    assertEquals(3, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors filtering by name - exact match")
  void testGetAuthorsFilteringByNameExactMatch() {
    String queryParams = "?page=0&size=10&name=J.K. Rowling";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(1, responseBody.content().size());
    assertEquals("J.K. Rowling", responseBody.content().getFirst().name());
    assertEquals(1, responseBody.totalElements());
    assertEquals(1, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors filtering by name - no results")
  void testGetAuthorsFilteringByNameNoResults() {
    String queryParams = "?page=0&size=10&name=NonExistentAuthorXYZ";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertTrue(responseBody.content().isEmpty());
    assertEquals(0, responseBody.totalElements());
    assertEquals(0, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors with minimum page size")
  void testGetAuthorsWithMinimumPageSize() {
    String queryParams = "?page=0&size=1";
    var response = restTemplate.exchange(
       AUTHORS_URL + queryParams,
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PageableResponse<GetAllAuthorsResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(1, responseBody.content().size());
    assertEquals(15, responseBody.totalElements());
    assertEquals(15, responseBody.totalPages());
  }

  @Test
  @DisplayName("Test GET authors returns correct content type")
  void testGetAuthorsContentType() {
    var response = restTemplate.exchange(
       AUTHORS_URL + "?page=0&size=5",
       HttpMethod.GET,
       null,
       new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getHeaders().getContentType());
    assertTrue(response.getHeaders().getContentType().includes(org.springframework.http.MediaType.APPLICATION_JSON));
  }
}
