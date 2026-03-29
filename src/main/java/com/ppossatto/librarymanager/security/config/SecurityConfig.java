package com.ppossatto.librarymanager.security.config;

import com.ppossatto.librarymanager.security.filter.JwtAuthFilter;
import com.ppossatto.librarymanager.security.userdetails.UserDetailsServiceImpl;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter filter;
  private final UserDetailsServiceImpl userDetailsService;

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
       .authorizeHttpRequests(req -> req
          .requestMatchers("/api/v1/login").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/v1/books").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/v1/books/**").hasRole(ROLE_USER)
          .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole(ROLE_LIBRARIAN)
          .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole(ROLE_USER, ROLE_LIBRARIAN)
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
}
