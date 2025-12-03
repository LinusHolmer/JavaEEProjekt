package com.example.JavaEE.controller;

import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import com.example.JavaEE.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;

    }

    @PutMapping("/change-username")
    public ResponseEntity<Void> changeUsername(@Valid @RequestBody ChangeUsernameDTO changeUsernameDTO) {
        adminService.changeUsername(changeUsernameDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        adminService.changePassword(changePasswordDTO);
        return ResponseEntity.noContent().build();
    }
}
