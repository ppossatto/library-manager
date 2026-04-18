package com.ppossatto.librarymanager.controller;

import com.ppossatto.librarymanager.dto.request.CreateAuthorRequest;
import com.ppossatto.librarymanager.dto.request.LoginRequest;
import com.ppossatto.librarymanager.dto.response.CreateAuthorResponse;
import com.ppossatto.librarymanager.dto.response.GetAllAuthorsResponse;
import com.ppossatto.librarymanager.dto.response.GetAuthorResponse;
import com.ppossatto.librarymanager.dto.response.LoginResponse;
import com.ppossatto.librarymanager.dto.response.PageableResponse;
import com.ppossatto.librarymanager.repository.AuthorsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

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
class AuthorsResourcesIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private AuthorsRepository authorsRepository;

  @Nested
  @DisplayName("GET /api/v1/authors")
  @Sql(
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
     scripts = "classpath:scripts/populate_authors.sql"
  )
  @Sql(
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
     scripts = "classpath:scripts/rollback_authors.sql"
  )
  class GetAllAuthors {
    private static final String GET_ALL_AUTHORS_URL = "/api/v1/authors";

    @Test
    @DisplayName("Test GET authors when has elements in the database")
    void testGetAuthorsWhenHasElementsInDatabase(){
      String queryParams = "?page=0&size=10";
      var response = restTemplate.exchange(
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + queryParams,
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
         GET_ALL_AUTHORS_URL + "?page=0&size=5",
         HttpMethod.GET,
         null,
         new ParameterizedTypeReference<PageableResponse<GetAllAuthorsResponse>>() {}
      );

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getHeaders().getContentType());
      assertTrue(response.getHeaders().getContentType().includes(org.springframework.http.MediaType.APPLICATION_JSON));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/authors/{id}")
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
        "classpath:scripts/rollback_books_and_authors.sql"
     }
  )
  class GetSingleAuthor {
    private static final String GET_SINGLE_AUTHOR_URL = "/api/v1/authors/%s";

    @Test
    @DisplayName("Get single author correctly")
    void getSingleAuthorCorrectly() {
      UUID authorId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
      var response = restTemplate.exchange(
         String.format(GET_SINGLE_AUTHOR_URL, authorId),
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
         String.format(GET_SINGLE_AUTHOR_URL, authorId),
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

  @Nested
  @DisplayName("POST /api/v1/authors")
  class CreateAuthor {
    private static final String CREATE_AUTHOR_URL = "/api/v1/authors";

    private static final String LOGIN_URL = "/api/v1/login";

    @Test
    @DisplayName("Create new author correctly")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_librarian.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void createAuthor() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("John")
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         CreateAuthorResponse.class
      );

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertTrue(response.getHeaders().containsHeader("location"));
      assertTrue(authorsRepository.existsByAuthorName("John"));
    }

    @Test
    @DisplayName("Verify forbidden create author with user role")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_user.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void verifyCreateAuthorErrorWithUserRole() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("John")
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-51290", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals("Permission not satisfied to access this resource", response.getBody().get("details"));
    }

    @Test
    @DisplayName("Verify when user is not authenticated")
    void verifyCreateAuthorErrorWithUserNotAuthenticated() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("John")
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-13409", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals("Authentication required", response.getBody().get("details"));
    }

    private String getBearerToken(String email, String password) {
      LoginRequest login = LoginRequest.builder()
         .email(email)
         .password(password)
         .build();
      var tokenRequest = restTemplate.exchange(
         LOGIN_URL, HttpMethod.POST,
         new HttpEntity<>(login), LoginResponse.class
      );

      assertNotNull(tokenRequest.getBody());
      return tokenRequest.getBody().tokenType() + " " + tokenRequest.getBody().token();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Verify when name field in request body is blank")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_librarian.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void verifyWhenNameFieldInRequestBodyIsBlank(String source) {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name(source)
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-99864", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals(Map.of("name", "The field name must be provided"), response.getBody().get("fields"));
    }

    @Test
    @DisplayName("Verify when name field has more than 150 characters")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_librarian.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void verifyWhenNameFieldHasMoreThan150Characters() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("ABC".repeat(51))
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-99864", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals(
         Map.of("name", "The name cannot have more than 150 characters"),
         response.getBody().get("fields")
      );
    }

    @Test
    @DisplayName("Verify when birthDate field is in future date")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_librarian.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void verifyWhenBirthDateFieldIsInFutureDate() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("John")
         .birthDate(LocalDate.now().plusDays(10))
         .nationality("French")
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-99864", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals(
         Map.of("birthDate", "The author birth date must be in a past date"),
         response.getBody().get("fields")
      );
    }

    @Test
    @DisplayName("Verify when nationality has more than 100 characters")
    @Sql(
       executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
       scripts = "classpath:scripts/populate_librarian.sql"
    )
    @Sql(
       executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
       scripts = "classpath:scripts/rollback_users.sql"
    )
    void verifyWhenNationalityHasMoreThan100Characters() {
      CreateAuthorRequest request = CreateAuthorRequest.builder()
         .name("John")
         .birthDate(LocalDate.of(1980, 1, 1))
         .nationality("French".repeat(20))
         .biography("Biography")
         .build();

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, getBearerToken("john.doe@email.com", "password"));

      HttpEntity<CreateAuthorRequest> requestEntity = new HttpEntity<>(request, headers);

      var response = restTemplate.exchange(
         CREATE_AUTHOR_URL,
         HttpMethod.POST,
         requestEntity,
         Map.class
      );

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("ERR-99864", response.getBody().get("errorCode"));
      assertEquals("WARNING", response.getBody().get("severity"));
      assertEquals(
         Map.of("nationality", "The nationality cannot have more than 100 characters"),
         response.getBody().get("fields")
      );
    }
  }
}
