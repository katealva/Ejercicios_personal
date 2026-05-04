package com.example.oreo2.security;

import com.example.oreo2.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            throw new AccessDeniedException("No authenticated user");
        if (!(auth.getPrincipal() instanceof User user))
            throw new AccessDeniedException("Principal is not a User entity");
        return user;
    }
}
