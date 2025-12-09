package com.example.JavaEE.controller;

import com.example.JavaEE.dto.RegisterRequest;
import com.example.JavaEE.service.CustomUserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class AuthController {


    private final CustomUserService service;

    @Autowired
    public AuthController(CustomUserService service) {

        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        service.registerUser(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, service.loginUser(registerRequest).toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, service.logoutUser().toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/checkRoles")
    public Map<String, Object> checkRoles(@RequestHeader("Authorization") String authHeader) {
        return Map.of(
                "roles", service.checkRoles(authHeader)
        );
    }
}

