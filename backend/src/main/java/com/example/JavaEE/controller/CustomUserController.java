package com.example.JavaEE.controller;

import com.example.JavaEE.dto.CustomUserDTO;
import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import com.example.JavaEE.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class CustomUserController {

    private final CustomUserService customUserService;
    private final CustomUserRepository customUserRepository;

    @Autowired
    public CustomUserController(CustomUserService customUserService, CustomUserRepository customUserRepository) {
        this.customUserService = customUserService;
        this.customUserRepository = customUserRepository;
    }

    @GetMapping("/Get-Users")
    public ResponseEntity<List<CustomUserDTO>> getUsers(Authentication auth) {
        CustomUser currentUser = customUserRepository.findByUsername(auth.getName());
        return ResponseEntity.ok(customUserService.getUsers(currentUser));
    }

}
