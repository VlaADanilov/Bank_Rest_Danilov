package com.example.bankcards.controller.filter;

import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.util.WorkWithJwt;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final WorkWithJwt jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null &&
                authHeader.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.parseClaims(token);
                UUID userId = jwtUtil.getUserIdFromToken(token);
                Role role = jwtUtil.getRoleFromToken(token);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        null, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));

                authentication.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
