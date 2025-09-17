package com.example.reservation_service.client.fallback;

import com.example.reservation_service.client.UserClient;
import com.example.reservation_service.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
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
