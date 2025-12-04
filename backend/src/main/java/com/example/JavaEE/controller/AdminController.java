package com.example.JavaEE.controller;

import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    //bara admin kan nå endpoints för metod i securityConfig (.requestMatchers("/admin/**").hasRole("ADMIN")

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // hämta alla användare
    @GetMapping("/users")
    public List<CustomUser> getUsers() {
        return adminService.getAllUsers();
    }

    // POST gör user till admin
    @PostMapping("/make-admin/{userId}")
    public CustomUser makeAdmin(@PathVariable String userId) {
        return adminService.promoteToAdmin(userId);
    }

    @PutMapping("/change-username")
    public ResponseEntity<Void> changeUsername (@Valid @RequestBody ChangeUsernameDTO changeUsernameDTO){
        adminService.changeUsername(changeUsernameDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword (@Valid @RequestBody ChangePasswordDTO changePasswordDTO){
        adminService.changePassword(changePasswordDTO);
        return ResponseEntity.noContent().build();
    }

    // DELETE  ta bort user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
