package com.example.user_service.mapper;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);           // Para requests (con password)
    User toEntity(UserDTO dto);         // Para crear/actualizar

    UserResponseDTO toResponseDTO(User user); // Para respuestas sin password
}