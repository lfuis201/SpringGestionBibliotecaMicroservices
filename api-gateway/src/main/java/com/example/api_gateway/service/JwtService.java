package com.example.api_gateway.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            throw new JwtException("Token expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("Unsupported token");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Malformed token");
        } catch (SecurityException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new JwtException("Invalid signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }

    public String extractUsername(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}