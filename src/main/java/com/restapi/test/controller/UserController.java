package com.restapi.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restapi.test.dto.LoginDTO;
import com.restapi.test.dto.UserDTO;
import com.restapi.test.responses.ApiResponse;
import com.restapi.test.responses.JwtResponse;
import com.restapi.test.responses.UserResponse;
import com.restapi.test.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserDTO userDTO) {
        try {
            UserResponse response = userService.register(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new ApiResponse<>(true, "User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(false, "Failed to register user: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> loginUser(@RequestBody LoginDTO loginDto) {
        try {
            String token = userService.loginUser(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", new JwtResponse(token)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new ApiResponse<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/google") 
    public ResponseEntity<?> googleLogin() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/oauth2/authorization/google")
                .build();
    }
}

