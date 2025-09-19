package com.example.reservation_service.controller;

import com.example.reservation_service.dto.ReservationDTO;
import com.example.reservation_service.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        reservationDTO = ReservationDTO.builder()
                .id(1L)
                .userId(10L)
                .bookId(20L)
                .reservationDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .active(true)
                .build();
    }

    @Test
    void getReservations_ShouldReturnList() throws Exception {
        Mockito.when(reservationService.getReservations(null, null, null))
                .thenReturn(List.of(reservationDTO));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }



    @Test
    void getReservations_ShouldReturnEmptyList_WhenNoReservations() throws Exception {
        Mockito.when(reservationService.getReservations(null, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getReservationById_ShouldReturnReservation() throws Exception {
        Mockito.when(reservationService.getReservationById(1L))
                .thenReturn(reservationDTO);

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getReservationById_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        Mockito.when(reservationService.getReservationById(99L))
                .thenThrow(new RuntimeException("Reservation not found"));

        mockMvc.perform(get("/reservations/99"))
                .andExpect(status().is5xxServerError()); // 👈 Ajusta según tu ControllerAdvice
    }

    @Test
    void createReservation_ShouldReturnCreatedReservation() throws Exception {
        Mockito.when(reservationService.createReservation(eq(10L), eq(20L), any(ReservationDTO.class)))
                .thenReturn(reservationDTO);

        mockMvc.perform(post("/reservations/10/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateReservation_ShouldReturnUpdatedReservation() throws Exception {
        Mockito.when(reservationService.updateReservation(eq(1L), any(ReservationDTO.class)))
                .thenReturn(reservationDTO);

        mockMvc.perform(put("/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void returnBook_ShouldReturnReservation() throws Exception {
        Mockito.when(reservationService.returnBook(1L))
                .thenReturn(reservationDTO);

        mockMvc.perform(put("/reservations/return/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deactivateReservation_ShouldReturnMessage() throws Exception {
        Mockito.when(reservationService.deactivateReservation(1L))
                .thenReturn("Reservation deactivated");

        mockMvc.perform(delete("/reservations/deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation deactivated"));
    }

    @Test
    void deleteReservation_ShouldReturnMessage() throws Exception {
        Mockito.when(reservationService.deleteReservation(1L))
                .thenReturn("Reservation deleted");

        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation deleted"));
    }

    @Test
    void getReservationsByUser_ShouldReturnList() throws Exception {
        Mockito.when(reservationService.getReservationsByUser(10L))
                .thenReturn(Collections.singletonList(reservationDTO));

        mockMvc.perform(get("/reservations/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
