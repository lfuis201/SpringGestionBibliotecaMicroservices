package com.example.auth_service.controller;

import com.example.auth_service.dto.UserRequest;
import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest userRequest) {
        String token = authService.login(userRequest.getEmail(), userRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest) {
        String token = authService.register(userRequest);
        return ResponseEntity.ok(token);
    }
}