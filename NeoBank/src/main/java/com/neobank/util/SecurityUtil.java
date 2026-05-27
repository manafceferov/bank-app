package com.neobank.util;

import com.neobank.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("UNAUTHORIZED");
        }
        User user = (User) auth.getPrincipal();
        return user.getId();
    }

    public static String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("UNAUTHORIZED");
        }
        User user = (User) auth.getPrincipal();
        return user.getEmail();
    }

    public static String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("UNAUTHORIZED");
        }
        User user = (User) auth.getPrincipal();
        return user.getRole().name();
    }
}