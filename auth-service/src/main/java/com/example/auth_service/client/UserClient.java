package com.example.auth_service.client;

import com.example.auth_service.dto.UserRequest;
import com.example.auth_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);

    @PostMapping("/users")
    UserResponse createUser(@RequestBody UserRequest userRequest);

    @PostMapping("/users/validate")
    boolean validateUser(@RequestBody UserRequest userRequest);
}