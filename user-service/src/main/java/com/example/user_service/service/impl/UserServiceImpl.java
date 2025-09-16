package com.example.user_service.service.impl;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.exception.BadRequestException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getAllUsersFallback")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }


    public List<UserDTO> getAllUsersFallback(Throwable t) {
        System.out.println("FALLBACK getAllUsers: " + t.getMessage());
        return List.of();
    }

    @Override
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getUserByIdFallback")
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO) // ✅ usar mapper
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserDTO getUserByIdFallback(Long id, Throwable t) {
        System.out.println("FALLBACK getUserById: " + t.getMessage());
        return UserDTO.builder()
                .id(id)
                .name("Unknown")
                .email("unknown@example.com")
                .build();
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already in use: " + userDTO.getEmail());
        }
        User user = userMapper.toEntity(userDTO); // ✅ usar mapper
        return userMapper.toDTO(userRepository.save(user)); // ✅ usar mapper
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

        return userMapper.toDTO(userRepository.save(existing)); // ✅ usar mapper
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(existing);
    }
}
