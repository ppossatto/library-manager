package com.ppossatto.librarymanager.security.service;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.expiration}")
  private Long expiration;

  private SecretKey secretKey;

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
         .verifyWith(secretKey)
         .build()
         .parseSignedClaims(token)
         .getPayload();
    } catch (JwtException je) {
      throw new CoreException(CoreExceptionType.JWT_PARSE_EXCEPTION);
    } catch (Exception e) {
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  @PostConstruct
  private void initSecretKey() {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String generateToken(UserDetails userDetails) {
    try {
      return Jwts.builder()
         .subject(userDetails.getUsername())
         .issuedAt(new Date())
         .expiration(new Date(System.currentTimeMillis() + expiration))
         .claim("authorities", getAuthoritiesFromUser(userDetails))
         .signWith(secretKey)
         .compact();
    } catch (JwtException je) {
      throw new CoreException(CoreExceptionType.JWT_CREATION_EXCEPTION);
    } catch (Exception e) {
      throw new CoreException(CoreExceptionType.GENERIC_ERROR, e);
    }
  }

  private static List<String> getAuthoritiesFromUser(UserDetails userDetails) {
    return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public Date getExpiration(String token) {
    return extractAllClaims(token).getExpiration();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    Claims claims = extractAllClaims(token);
    return claims.getSubject().equals(userDetails.getUsername())
       && new Date().before(claims.getExpiration());
  }

  private boolean isTokenExpired(String token) {
    return new Date().after(extractAllClaims(token).getExpiration());
  }
}
