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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private BookClient bookClient;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private UserDTO userDTO;
    private BookDTO bookDTO;
    private Reservation reservation;
    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);

        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setActive(true);
        bookDTO.setAvailable(true);
        bookDTO.setUnits(5);

        reservation = Reservation.builder()
                .id(1L)
                .userId(1L)
                .bookId(1L)
                .reservationDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .active(true)
                .build();

        reservationDTO = new ReservationDTO();
        reservationDTO.setReturnDate(LocalDate.now().plusDays(7));
    }

    @Test
    void createReservation_ShouldReturnReservationDTO_WhenBookAndUserValid() {
        // Arrange
        when(userClient.getUserById(1L)).thenReturn(userDTO);
        when(bookClient.getBookById(1L)).thenReturn(bookDTO);
        when(bookClient.reduceBookUnits(1L)).thenReturn(bookDTO);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTO);

        // Act
        ReservationDTO result = reservationService.createReservation(1L, 1L, reservationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(reservationDTO.getReturnDate(), result.getReturnDate());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_ShouldThrowException_WhenBookInactive() {
        bookDTO.setActive(false);
        when(userClient.getUserById(1L)).thenReturn(userDTO);
        when(bookClient.getBookById(1L)).thenReturn(bookDTO);

        assertThrows(BadRequestException.class,
                () -> reservationService.createReservation(1L, 1L, reservationDTO));
    }

    @Test
    void returnBook_ShouldReturnReservationDTO_WhenValidReservation() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTO);

        // Act
        ReservationDTO result = reservationService.returnBook(1L);

        // Assert
        assertNotNull(result);
        assertEquals(reservationDTO.getReturnDate(), result.getReturnDate());
        verify(bookClient, times(1)).increaseBookUnits(1L);
    }

    @Test
    void returnBook_ShouldThrowException_WhenReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.returnBook(99L));
    }
    @Test
    void createReservation_ShouldThrowException_WhenBookUnavailable() {
        bookDTO.setAvailable(false);
        when(userClient.getUserById(1L)).thenReturn(userDTO);
        when(bookClient.getBookById(1L)).thenReturn(bookDTO);

        assertThrows(BadRequestException.class,
                () -> reservationService.createReservation(1L, 1L, reservationDTO));
    }

    @Test
    void returnBook_ShouldThrowException_WhenReservationAlreadyInactive() {
        reservation.setActive(false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(BadRequestException.class,
                () -> reservationService.returnBook(1L));
    }

    @Test
    void returnBook_ShouldDeactivateReservation_WhenValidReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTO);

        ReservationDTO result = reservationService.returnBook(1L);

        assertNotNull(result);
        assertFalse(reservation.isActive(), "Reservation should be deactivated after return");
        verify(bookClient, times(1)).increaseBookUnits(1L);
    }

    @Test
    void createReservation_ShouldCallReduceBookUnits_WhenValidReservation() {
        when(userClient.getUserById(1L)).thenReturn(userDTO);
        when(bookClient.getBookById(1L)).thenReturn(bookDTO);
        when(bookClient.reduceBookUnits(1L)).thenReturn(bookDTO);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTO);

        reservationService.createReservation(1L, 1L, reservationDTO);

        verify(bookClient, times(1)).reduceBookUnits(1L);
    }

}
