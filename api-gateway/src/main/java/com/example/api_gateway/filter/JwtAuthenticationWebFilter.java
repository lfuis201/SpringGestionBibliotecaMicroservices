package com.example.api_gateway.filter;

import com.example.api_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();

        // Permitir acceso a rutas públicas
        if (isPublicEndpoint(path, method)) {
            log.debug("Public endpoint accessed: {} {}", method, path);
            return chain.filter(exchange);
        }

        // Extraer token del header Authorization
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            log.warn("No token provided for protected endpoint: {} {}", method, path);
            return unauthorizedResponse(exchange, "Token not provided");
        }

        try {
            return authenticateToken(token)
                    .flatMap(auth -> {
                        log.debug("User {} authenticated successfully for: {} {}", auth.getName(), method, path);

                        // Agregar información del usuario autenticado en los headers
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", auth.getName())
                                .header("X-Authenticated", "true")
                                .build();

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(modifiedRequest)
                                .build();

                        return chain.filter(modifiedExchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    })
                    .onErrorResume(JwtException.class, ex -> {
                        log.error("JWT validation failed: {}", ex.getMessage());
                        return unauthorizedResponse(exchange, "Invalid token: " + ex.getMessage());
                    });
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Authentication failed");
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        // Rutas de autenticación - siempre públicas
        if (path.startsWith("/auth/")) return true;

        // Health checks - siempre públicos
        if (path.startsWith("/actuator/")) return true;
        if (path.equals("/error")) return true;

        // Rutas específicas de usuarios para FeignClient
        if (path.startsWith("/users/email/")) return true;
        if (path.equals("/users/validate")) return true;

        // POST /users para crear usuarios (FeignClient)
        if (path.equals("/users") && "POST".equals(method)) return true;

        return false;
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<UsernamePasswordAuthenticationToken> authenticateToken(String token) {
        return Mono.fromCallable(() -> {
            Jws<Claims> claims = jwtService.validateToken(token);
            String username = claims.getBody().getSubject();

            if (StringUtils.hasText(username)) {
                return new UsernamePasswordAuthenticationToken(username, null, List.of());
            }
            throw new JwtException("Invalid token subject");
        });
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = "{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}";
        var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}