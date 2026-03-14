package com.ppossatto.librarymanager.security.filter;

import com.ppossatto.librarymanager.security.service.JwtService;
import com.ppossatto.librarymanager.security.userdetails.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
     HttpServletRequest request,
     HttpServletResponse response,
     FilterChain filterChain) throws ServletException, IOException {

    log.debug("Processing request: {}", request.getRequestURI());
    log.debug("Authorization header: {}", request.getHeader(HttpHeaders.AUTHORIZATION));

    String jwtHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    String jwtToken = jwtHeader.substring(7);
    String email = jwtService.extractUsername(jwtToken);
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      if (!jwtService.isTokenValid(jwtToken, userDetails)) {
        filterChain.doFilter(request, response);
        return;
      }
      UsernamePasswordAuthenticationToken authToken =
         new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
         );

      authToken.setDetails(
         new WebAuthenticationDetailsSource().buildDetails(request)
      );

      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);
  }
}
