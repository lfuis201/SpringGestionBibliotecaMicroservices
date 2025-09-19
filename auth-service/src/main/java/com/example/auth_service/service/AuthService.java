package com.example.auth_service.service;


import com.example.auth_service.client.UserClient;
import com.example.auth_service.dto.UserRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String email, String password) {
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword(password);

        boolean isValid = userClient.validateUser(request);
        if (!isValid) {
            throw new CustomException("Credenciales inválidas");
        }

        // Obtener el usuario para generar token
        UserResponse user = userClient.getUserByEmail(email);
        return jwtService.generateToken(user);
    }

    public String register(UserRequest userRequest) {
        // Hashear la contraseña antes de enviar al user-service
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashedPassword);

        // Crear el usuario usando el UserClient
        UserResponse createdUser = userClient.createUser(userRequest);
        if (createdUser == null) {
            throw new CustomException("Error al registrar usuario");
        }

        // Generar token para login automático
        return jwtService.generateToken(createdUser);
    }

    private boolean passwordMatches(String raw, String hash) {
        return passwordEncoder.matches(raw, hash);
    }
}