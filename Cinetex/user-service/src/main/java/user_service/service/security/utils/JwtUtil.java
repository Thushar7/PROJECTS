package user_service.service.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import user_service.service.constants.AppConstants;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${security.jwt.secret:${jwt.secret}}") String secret,
                   @Value("${security.jwt.expiration:3600000}") long expirationMs) {
        this.key = buildKey(secret);
        this.expirationMs = expirationMs;
        log.debug("[JWT] Initialized key bytes={} expMs={}", key.getEncoded().length, expirationMs);
    }

    private SecretKey buildKey(String secret) {
        try {
            byte[] decoded = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(decoded);
        } catch (RuntimeException ex) {
            byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) { // pad deterministically
                byte[] padded = new byte[32];
                System.arraycopy(bytes, 0, padded, 0, bytes.length);
                for (int i = bytes.length; i < 32; i++) padded[i] = (byte) (23 + i * 11);
                return Keys.hmacShaKeyFor(padded);
            }
            return Keys.hmacShaKeyFor(bytes);
        }
    }

    public String generateToken(String username, Long userId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        Map<String,Object> claims = new HashMap<>();
        claims.put(AppConstants.JWT_CLAIM_USER_ID, userId);
        claims.put(AppConstants.JWT_CLAIM_ROLE, role);
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key) // 0.12.x infers HS algorithm
                .compact();
        log.debug("[JWT] Generated token sub={} role={} exp={}", username, role, exp);
        return token;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims c = extractAllClaims(token);
            boolean valid = c.getExpiration() != null && c.getExpiration().after(new Date());
            if (!valid) log.warn("[JWT] Token expired exp={}", c.getExpiration());
            return valid;
        } catch (Exception e) {
            log.warn("[JWT] Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        var parsed = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        return parsed.getPayload();
    }
}
