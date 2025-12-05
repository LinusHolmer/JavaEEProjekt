package com.example.JavaEE.controller;

import com.example.JavaEE.dto.RegisterRequest;
import com.example.JavaEE.service.CustomUserService;
import com.example.JavaEE.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CustomUserService service;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, CustomUserService service) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
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

