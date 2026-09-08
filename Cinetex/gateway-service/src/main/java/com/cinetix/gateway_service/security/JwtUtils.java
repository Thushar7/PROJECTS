package com.cinetix.gateway_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class JwtUtils {
    private SecretKey key;

    // Corrected property name from secreteJwtString to jwt.secret
    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    private void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("JWT secret key initialized (length: {} chars)", jwtSecret.length());
    }

    public String getUsernameFromToken(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts all claims from the JWT token.
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validates the token against the provided user details.
     */
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return Objects.equals(tokenUsername, username) && !isTokenExpired(token);
    }

    /**
     * Checks if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            log.warn("Token expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object rawRole = claims.get("role");
        List<String> roles = new ArrayList<>();
        if (rawRole instanceof String roleStr) {
            roles.add(roleStr.trim());
        } else if (rawRole instanceof List<?> roleList) {
            for (Object r : roleList) {
                if (r instanceof String roleItem) {
                    roles.add(roleItem.trim());
                }
            }
        }
        return roles;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
