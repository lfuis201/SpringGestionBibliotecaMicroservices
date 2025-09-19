package com.example.reservation_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDTOTest {

    @Test
    void builder_ShouldCreateCorrectObject() {
        LocalDate now = LocalDate.now();

        ReservationDTO dto = ReservationDTO.builder()
                .id(1L)
                .userId(10L)
                .bookId(20L)
                .reservationDate(now)
                .returnDate(now.plusDays(7))
                .active(true)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getUserId());
        assertTrue(dto.isActive());
    }

    @Test
    void equalsAndHashCode_ShouldWork() {
        ReservationDTO dto1 = ReservationDTO.builder().id(1L).build();
        ReservationDTO dto2 = ReservationDTO.builder().id(1L).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldNotBeNull() {
        ReservationDTO dto = ReservationDTO.builder().id(1L).build();
        assertNotNull(dto.toString());
    }
}
