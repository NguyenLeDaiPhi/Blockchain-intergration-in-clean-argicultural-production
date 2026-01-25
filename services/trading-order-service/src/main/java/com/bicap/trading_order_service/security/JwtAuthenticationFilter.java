package com.bicap.trading_order_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain 
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        Claims claims = jwtUtils.parseClaims(token);

        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = claims.getSubject();
        String email = claims.get("email", String.class);

        // Handle roles as List or String
        List<String> roles = new ArrayList<>();
        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof String) {
            roles.add((String) rolesObj);
        } else if (rolesObj instanceof List) {
            roles = ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        if (roles.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        List<SimpleGrantedAuthority> authorities =
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        JwtUser jwtUser = new JwtUser(
                username,
                email,
                roles
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
