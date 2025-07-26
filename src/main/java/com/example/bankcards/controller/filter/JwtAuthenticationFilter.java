package com.example.bankcards.controller.filter;

import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.util.WorkWithJwt;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final WorkWithJwt jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        log.debug("Processing authentication for URI: %s".formatted(request.getRequestURI()));

        if (authHeader != null &&
                authHeader.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Authorization header found with Bearer token");
            String token = authHeader.substring(7);
            log.debug("Extracted JWT token from header");

            if (jwtUtil.validateToken(token)) {
                log.debug("JWT token is valid, parsing claims");
                Claims claims = jwtUtil.parseClaims(token);
                UUID userId = jwtUtil.getUserIdFromToken(token);
                Role role = jwtUtil.getRoleFromToken(token);

                log.info("Successfully authenticated user with ID: %s and role: %s".formatted(userId, role));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        null, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));

                authentication.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authentication set in SecurityContext for user: %s".formatted(userId));
            } else {
                log.warn("JWT token validation failed for URI: %s".formatted(request.getRequestURI()));
            }
        } else {
            if (authHeader == null) {
                log.debug("No Authorization header found for URI: %s".formatted(request.getRequestURI()));
            } else if (!authHeader.startsWith("Bearer ")) {
                log.debug("Authorization header doesn't start with 'Bearer ' for URI: %s".formatted(request.getRequestURI()));
            } else {
                log.debug("Authentication already exists in SecurityContext, skipping JWT processing for URI: %s".formatted(request.getRequestURI()));
            }
        }

        filterChain.doFilter(request, response);
    }
}
