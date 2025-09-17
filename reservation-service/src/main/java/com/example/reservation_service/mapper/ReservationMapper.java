package com.example.reservation_service.mapper;

import com.example.reservation_service.dto.ReservationDTO;
import com.example.reservation_service.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationDTO toDTO(Reservation reservation);
    Reservation toEntity(ReservationDTO dto);
}
