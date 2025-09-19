package com.example.user_service.controller;


import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserLoginRequest;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET all users
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET user by id
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // POST create new user
    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    // PUT update existing user
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id,
                              @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @GetMapping("/email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }


    // DELETE user by id
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/validate")
    public boolean validateUser(@RequestBody UserLoginRequest request) {
        return userService.validateUser(request.getEmail(), request.getPassword());
    }


}
