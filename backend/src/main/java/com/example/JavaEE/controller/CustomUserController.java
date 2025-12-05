package com.example.JavaEE.controller;

import com.example.JavaEE.dto.CustomUserDTO;
import com.example.JavaEE.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class CustomUserController {

    private final CustomUserService customUserService;

    @Autowired
    public CustomUserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @GetMapping("/Get-Users")
    public ResponseEntity<List<CustomUserDTO>> getUsers() {
        return ResponseEntity.ok(customUserService.getUsers());
    }

}
