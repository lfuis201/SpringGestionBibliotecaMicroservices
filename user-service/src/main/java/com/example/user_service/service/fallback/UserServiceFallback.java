package com.example.user_service.service.fallback;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceFallback {


    public List<UserResponseDTO> getAllUsers() {
        System.out.println("FALLBACK getAllUsers triggered");
        return List.of(); // Lista vacía
    }



    public UserResponseDTO getUserById(Long id) {
        System.out.println("FALLBACK getUserById triggered for id: " + id);
        throw new ResourceNotFoundException("User with id " + id + " not found (fallback triggered)");
    }



    public UserResponseDTO saveUser(UserDTO userDTO) {
        throw new UnsupportedOperationException("Cannot save user during fallback");
    }

    public UserResponseDTO updateUser(Long id, UserDTO userDTO) {
        throw new UnsupportedOperationException("Cannot update user during fallback");
    }

    public void deleteUser(Long id) {
        throw new UnsupportedOperationException("Cannot delete user during fallback");
    }


    public UserResponseDTO getUserByEmail(String email) {
        System.out.println("FALLBACK getUserByEmail triggered for email: " + email);
        throw new ResourceNotFoundException("User with email " + email + " not found (fallback triggered)");
    }


}
