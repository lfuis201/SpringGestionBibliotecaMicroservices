package com.example.reservation_service.service.impl;

import com.example.reservation_service.client.BookClient;
import com.example.reservation_service.client.UserClient;
import com.example.reservation_service.dto.BookDTO;
import com.example.reservation_service.dto.ReservationDTO;
import com.example.reservation_service.dto.UserDTO;
import com.example.reservation_service.exception.BadRequestException;
import com.example.reservation_service.exception.ResourceNotFoundException;
import com.example.reservation_service.mapper.ReservationMapper;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.repository.ReservationRepository;
import com.example.reservation_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final ReservationMapper reservationMapper;

    @Override
    public List<ReservationDTO> getReservations(Boolean active, LocalDate fromDate, LocalDate toDate) {
        return reservationRepository.findAll().stream()
                .filter(res -> active == null || res.isActive() == active)
                .filter(res -> fromDate == null || !res.getReservationDate().isBefore(fromDate))
                .filter(res -> toDate == null || !res.getReservationDate().isAfter(toDate))
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO createReservation(Long userId, Long bookId, ReservationDTO reservationDTO) {
        // 1. Validar existencia de user y book vía Feign
        UserDTO user = userClient.getUserById(userId);
        BookDTO book = bookClient.getBookById(bookId);

        if (!book.isActive()) {
            throw new BadRequestException("Book with id " + bookId + " is inactive");
        }

        if (!book.isAvailable()) {
            throw new BadRequestException("Book with id " + bookId + " has no available units");
        }

        // 2. Reducir stock del libro en book-service
        BookDTO updatedBook = bookClient.reduceBookUnits(bookId);
        if (updatedBook.getUnits() < 0) {
            throw new BadRequestException("Book with id " + bookId + " could not reduce units");
        }

        // 3. Crear la reserva
        Reservation reservation = Reservation.builder()
                .userId(user.getId())
                .bookId(updatedBook.getId())
                .reservationDate(LocalDate.now())
                .returnDate(reservationDTO.getReturnDate())
                .active(true)
                .build();

        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }


    @Override
    public ReservationDTO returnBook(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + reservationId));

        if (!reservation.isActive()) {
            throw new BadRequestException("Reservation is already closed");
        }

        // 1. Marcar como devuelta
        reservation.setActive(false);
        reservation.setReturnDate(LocalDate.now());

        // 2. Incrementar stock del libro en book-service
        bookClient.increaseBookUnits(reservation.getBookId());

        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    @Override
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));
        return reservationMapper.toDTO(reservation);
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));

        if (!reservation.isActive()) {
            throw new BadRequestException("Cannot update an inactive reservation");
        }

        reservation.setReturnDate(reservationDTO.getReturnDate());
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    @Override
    public String deactivateReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));

        if (!reservation.isActive()) {
            throw new BadRequestException("Reservation is already inactive");
        }

        reservation.setActive(false);
        reservation.setReturnDate(LocalDate.now());
        reservationRepository.save(reservation);

        return "Reservation with id " + id + " has been deactivated successfully.";
    }

    @Override
    public String deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));
        reservationRepository.delete(reservation);
        return "Reservation with id " + id + " has been permanently deleted.";
    }

    @Override
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        // validar que el usuario exista en user-service
        userClient.getUserById(userId);

        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for user with id " + userId);
        }

        return reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }
}