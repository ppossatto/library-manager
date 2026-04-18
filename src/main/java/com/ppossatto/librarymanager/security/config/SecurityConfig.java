package com.ppossatto.librarymanager.security.config;

import com.ppossatto.librarymanager.exception.enums.SeverityType;
import com.ppossatto.librarymanager.security.filter.JwtAuthFilter;
import com.ppossatto.librarymanager.security.userdetails.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter filter;
  private final UserDetailsServiceImpl userDetailsService;
  private final ObjectMapper objectMapper;

  private static final String ROLE_USER = "USER";
  private static final String ROLE_LIBRARIAN = "LIBRARIAN";

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
       .exceptionHandling(exceptions ->
          exceptions
             .authenticationEntryPoint(customAuthenticationEntryPoint())
             .accessDeniedHandler(customAccessDeniedHandler())
       )
       .authorizeHttpRequests(req -> req
          .requestMatchers("/api/v1/login").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/password").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/block").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/role").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/email").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/books").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/v1/books", "/api/v1/books/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/books/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/authors").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/v1/authors/**").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/v1/authors").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/authors/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.DELETE, "/api/v1/authors/**").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/reservations").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/reservations/my").hasRole(ROLE_USER)
          .requestMatchers(HttpMethod.GET, "/api/v1/reservations/**").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.POST, "/api/v1/reservations").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/return").hasRole(ROLE_LIBRARIAN)
          .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
          .anyRequest().authenticated()
       ).sessionManagement(
          session ->
             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
       ).authenticationProvider(
          authenticationProvider()
       ).addFilterBefore(
          filter, UsernamePasswordAuthenticationFilter.class
       );
    return http.build();
  }

  @Bean
  public AuthenticationEntryPoint customAuthenticationEntryPoint() {
    return ((request, response, authException) -> {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      Map<String, Object> errorResponse = new LinkedHashMap<>();
      errorResponse.put("severity", SeverityType.WARNING.name());

      String jwtError = (String) request.getAttribute("jwt_error");
      if (jwtError != null) {
        errorResponse.put("errorCode", "ERR-94501");
        errorResponse.put("details", "Invalid or expired token");
      } else {
        errorResponse.put("errorCode", "ERR-13409");
        errorResponse.put("details", "Authentication required");
      }

      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    });
  }

  @Bean
  public AccessDeniedHandler customAccessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      Map<String, Object> errorResponse = new LinkedHashMap<>();
      errorResponse.put("errorCode", "ERR-51290");
      errorResponse.put("severity", SeverityType.WARNING.name());
      errorResponse.put("details", "Permission not satisfied to access this resource");

      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    };
  }
}
