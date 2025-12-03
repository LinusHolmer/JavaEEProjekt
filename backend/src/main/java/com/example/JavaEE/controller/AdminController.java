package com.example.JavaEE.controller;

import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    //bara admin kan nå endpoints för metod i securityConfig (.requestMatchers("/admin/**").hasRole("ADMIN")

    private final AdminService adminService;

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

    // DELETE  ta bort user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
