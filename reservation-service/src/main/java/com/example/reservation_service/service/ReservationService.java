package com.example.reservation_service.service;


import com.example.reservation_service.dto.ReservationDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getReservations(Boolean active, LocalDate fromDate, LocalDate toDate);
    ReservationDTO createReservation(Long userId, Long bookId, ReservationDTO reservationDTO);
    ReservationDTO returnBook(Long reservationId);
    ReservationDTO getReservationById(Long id);
    ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO);
    String deactivateReservation(Long id);
    String deleteReservation(Long id);
    List<ReservationDTO> getReservationsByUser(Long userId);
}