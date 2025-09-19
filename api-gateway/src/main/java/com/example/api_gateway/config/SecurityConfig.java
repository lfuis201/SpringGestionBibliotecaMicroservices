package com.example.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import com.example.api_gateway.filter.JwtAuthenticationWebFilter;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Rutas públicas - SIN TOKEN
                        .pathMatchers("/auth/**").permitAll()

                        // Rutas específicas públicas para FeignClient SOLAMENTE
                        .pathMatchers("/users/email/**").permitAll()
                        .pathMatchers("/users/validate").permitAll()
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/users").permitAll()

                        // Health checks
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/error").permitAll()

                        // Rutas protegidas - REQUIEREN TOKEN
                        .pathMatchers("/api/**").authenticated()

                        // IMPORTANTE: Todas las demás rutas /users/* requieren autenticación
                        .pathMatchers("/users/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}