package com.bicap.trading_order_service.security.config;

import com.bicap.trading_order_service.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth

            // PUBLIC
            .requestMatchers("/actuator/**").permitAll()

            // ===== RETAILER =====
            // Tạo đơn
            .requestMatchers(HttpMethod.POST, "/api/orders")
                .hasAuthority("ROLE_RETAILER")

            // Test JWT
            .requestMatchers(HttpMethod.GET, "/api/orders/me")
                .authenticated()

            // ===== FARM =====
            .requestMatchers(HttpMethod.GET, "/api/orders/by-farm/**")
                .hasAuthority("ROLE_FARM_MANAGER")

            .requestMatchers(
                HttpMethod.PUT,
                "/api/orders/*/confirm",
                "/api/orders/*/reject"
            ).hasAuthority("ROLE_FARM_MANAGER")

            // ===== ADMIN =====
            .requestMatchers(HttpMethod.PUT, "/api/orders/*/complete")
                .hasAuthority("ROLE_ADMIN")

            .anyRequest().authenticated()
        )
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}


}
