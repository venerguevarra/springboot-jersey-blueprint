package com.doesitwork.springboot.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationUtil {
    public static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}
