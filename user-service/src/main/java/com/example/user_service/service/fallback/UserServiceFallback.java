package com.example.user_service.service.fallback;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceFallback implements UserService {


    @Override
    public List<UserDTO> getAllUsers() {
        System.out.println("FALLBACK getAllUsers triggered");
        return List.of(); // Lista vacía
    }


    @Override
    public UserDTO getUserById(Long id) {
        System.out.println("FALLBACK getUserById triggered for id: " + id);
        throw new ResourceNotFoundException("User with id " + id + " not found (fallback triggered)");
    }


    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        throw new UnsupportedOperationException("Cannot save user during fallback");
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        throw new UnsupportedOperationException("Cannot update user during fallback");
    }

    @Override
    public void deleteUser(Long id) {
        throw new UnsupportedOperationException("Cannot delete user during fallback");
    }
}
