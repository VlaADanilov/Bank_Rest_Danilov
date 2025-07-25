package com.example.bankcards.util;

import com.example.bankcards.entity.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserReturner {

    public UUID getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        return (UUID) details;
    }

    public Role getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority next = auth.getAuthorities().iterator().next();
        return Role.valueOf(next.getAuthority().replace("ROLE_", ""));
    }
}
