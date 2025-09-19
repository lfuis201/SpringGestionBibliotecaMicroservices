package com.example.user_service.service.impl;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.exception.BadRequestException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import com.example.user_service.service.fallback.UserServiceFallback;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserServiceFallback fallback;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallbackGetAllUsers")
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO) // No exponemos password
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> fallbackGetAllUsers(Throwable t) {
        return fallback.getAllUsers();
    }

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallbackGetUserById")
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserResponseDTO fallbackGetUserById(Long id, Throwable t) {
        return fallback.getUserById(id);
    }

    @Override
    public UserResponseDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already in use: " + userDTO.getEmail());
        }
        User user = userMapper.toEntity(userDTO);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserDTO userDTO) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (!existing.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already in use: " + userDTO.getEmail());
        }

        existing.setName(userDTO.getName());
        existing.setEmail(userDTO.getEmail());
        existing.setPassword(userDTO.getPassword());

        return userMapper.toResponseDTO(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(existing);
    }

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallbackGetUserByEmail")
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    public UserResponseDTO fallbackGetUserByEmail(String email, Throwable t) {
        return fallback.getUserByEmail(email);
    }

    @Override
    public boolean validateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

}