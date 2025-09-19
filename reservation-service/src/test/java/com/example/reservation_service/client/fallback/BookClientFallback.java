package com.example.reservation_service.client.fallback;

import com.example.reservation_service.dto.BookDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class BookClientFallbackTest {

    @InjectMocks
    private BookClientFallback bookClientFallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBookById_ShouldReturnUnavailableBook() {
        Long bookId = 1L;

        BookDTO result = bookClientFallback.getBookById(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Unavailable Book", result.getTitle());
        assertEquals("N/A", result.getAuthor());
        assertFalse(result.isActive());
        assertFalse(result.isAvailable());
    }

    @Test
    void reduceBookUnits_ShouldReturnUnavailableBook() {
        Long bookId = 2L;

        BookDTO result = bookClientFallback.reduceBookUnits(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Unavailable Book", result.getTitle());
        assertEquals("N/A", result.getAuthor());
        assertFalse(result.isActive());
        assertFalse(result.isAvailable());
    }

    @Test
    void increaseBookUnits_ShouldReturnUnavailableBook() {
        Long bookId = 3L;

        BookDTO result = bookClientFallback.increaseBookUnits(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Unavailable Book", result.getTitle());
        assertEquals("N/A", result.getAuthor());
        assertFalse(result.isActive());
        assertFalse(result.isAvailable());
    }

    // 🔹 Caso extra: cuando se pasa null
    @Test
    void getBookById_WithNullId_ShouldStillReturnUnavailableBook() {
        BookDTO result = bookClientFallback.getBookById(null);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Unavailable Book", result.getTitle());
        assertEquals("N/A", result.getAuthor());
        assertFalse(result.isActive());
        assertFalse(result.isAvailable());
    }

    // 🔹 Validar que las tres llamadas devuelven objetos distintos (no compartidos)
    @Test
    void methods_ShouldReturnNewInstancesEachTime() {
        BookDTO b1 = bookClientFallback.getBookById(10L);
        BookDTO b2 = bookClientFallback.reduceBookUnits(10L);
        BookDTO b3 = bookClientFallback.increaseBookUnits(10L);

        assertNotSame(b1, b2);
        assertNotSame(b2, b3);
        assertNotSame(b1, b3);
    }
}
