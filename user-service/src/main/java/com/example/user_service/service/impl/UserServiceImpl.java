package com.example.user_service.service.impl;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.exception.BadRequestException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User mapToEntity(UserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getAllUsersFallback")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllUsersFallback(Throwable t) {
        // Retorna lista vacía o algún mensaje
        System.out.println("FALLBACK getAllUsers: " + t.getMessage());
        return List.of();
    }

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getUserByIdFallback")
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserDTO getUserByIdFallback(Long id, Throwable t) {
        System.out.println("FALLBACK getUserById: " + t.getMessage());
        return new UserDTO("Unknown", "unknown@example.com");
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        if(userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already in use: " + userDTO.getEmail());
        }
        User user = mapToEntity(userDTO);
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        if (!existing.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already in use: " + userDTO.getEmail());
        }
        existing.setName(userDTO.getName());
        existing.setEmail(userDTO.getEmail());
        return mapToDTO(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(existing);
    }
}