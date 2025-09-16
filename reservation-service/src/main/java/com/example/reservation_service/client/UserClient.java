package com.example.reservation_service.client;

import com.example.reservation_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/users",
        fallback = UserClientFallback.class
)
public interface UserClient {
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}

@Component
class UserClientFallback implements UserClient {
    @Override
    public UserDTO getUserById(Long id) {
        // puedes devolver un DTO por defecto o lanzar una excepción controlada
        return UserDTO.builder()
                .id(id)
                .name("Unknown User")
                .email("unavailable@fallback.com")
                .build();
    }
}
