package com.cinetix.gateway_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException {

        try {
            log.debug("Starting JWT authentication filter for request: {}", request.getRequestURI());
            String token = getTokenFromRequest(request);

            if (token != null) {
                String username = jwtUtils.getUsernameFromToken(token);

                if (!StringUtils.hasText(username)) {
                    log.warn("JWT token does not contain a username.");
                    respondUnauthorized(response, "Invalid JWT token: username missing");
                    return;
                }

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Extract roles from JWT
                    List<String> roles = jwtUtils.getRolesFromToken(token);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> "ROLE" + role.toUpperCase())
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    if (jwtUtils.isTokenValid(token, username)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        log.warn("Invalid or expired JWT token for user: {}", username);
                        respondUnauthorized(response, "Invalid or expired JWT token");
                        return;
                    }
                }
            } else {
                log.warn("No JWT token found in request header.");
            }

            filterChain.doFilter(request, response);
            log.debug("JWT authentication filter completed for request: {}", request.getRequestURI());

        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
            respondUnauthorized(response, "JWT token has expired");
        } catch (MalformedJwtException | SignatureException ex) {
            log.warn("Malformed or invalid JWT token: {}", ex.getMessage());
            respondUnauthorized(response, "Malformed or invalid JWT token");
        } catch (Exception ex) {
            log.error("Exception during JWT token processing: {}", ex.getMessage(), ex);
            respondUnauthorized(response, "Error processing JWT token: " + ex.getMessage());
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }

    // Simple error response DTO
    record ErrorResponse(String message) {}
}
