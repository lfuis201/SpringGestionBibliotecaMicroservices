package com.example.reservation_service.client.fallback;

import com.example.reservation_service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserClientFallbackTest {

    @InjectMocks
    private UserClientFallback userClientFallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_ShouldReturnFallbackUser() {
        Long userId = 10L;

        UserDTO result = userClientFallback.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Unknown User", result.getName());
        assertEquals("unavailable@fallback.com", result.getEmail());
    }

    // 🔹 Caso extra: cuando se pasa null
    @Test
    void getUserById_WithNullId_ShouldStillReturnFallbackUser() {
        UserDTO result = userClientFallback.getUserById(null);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Unknown User", result.getName());
        assertEquals("unavailable@fallback.com", result.getEmail());
    }

    // 🔹 Validar que las llamadas devuelven instancias distintas
    @Test
    void getUserById_ShouldReturnNewInstancesEachTime() {
        UserDTO u1 = userClientFallback.getUserById(1L);
        UserDTO u2 = userClientFallback.getUserById(1L);

        assertNotSame(u1, u2);
    }

    // 🔹 Crear directamente la clase sin mocks (constructor vacío)
    @Test
    void constructor_ShouldInstantiateFallbackDirectly() {
        UserClientFallback directInstance = new UserClientFallback();

        UserDTO result = directInstance.getUserById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Unknown User", result.getName());
    }
}
