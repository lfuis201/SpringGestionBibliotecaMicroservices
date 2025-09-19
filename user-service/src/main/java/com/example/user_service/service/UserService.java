package com.example.user_service.service;


import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO saveUser(UserDTO userDTO);
    UserResponseDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserResponseDTO getUserByEmail(String email);
    boolean validateUser(String email, String password);

}