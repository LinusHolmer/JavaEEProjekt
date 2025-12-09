package com.example.JavaEE.service;

import com.example.JavaEE.jwt.JwtAuthFilter;
import com.example.JavaEE.model.CustomUser;

import com.example.JavaEE.repository.CustomUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);


    @Autowired
    public AdminService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // hämta alla users
    public List<CustomUser> getAllUsers() {
        return customUserRepository.findAll();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public CustomUser promoteToAdmin(String userId) {
        CustomUser currentUser = getCurrentUserOrThrow();
        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can promote users");
        }

        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> roles = user.getRoles() == null
                ? new HashSet<>()
                : new HashSet<>(user.getRoles());

        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        logger.info("Successfully promoted user: {} to admin by admin: {}", user.getUsername(), currentUser.getUsername());
        return customUserRepository.save(user);
    }

    public void changeUsername(ChangeUsernameDTO changeUsernameDTO) {
        // Användaren som är inloggad just nu
        CustomUser currentUser = getCurrentUserOrThrow();

        // Användaren som ska ändras
        CustomUser targetUser = customUserRepository.findByUsername(changeUsernameDTO.username());
        if (targetUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }



        // Admin får ändra vem som helst
        boolean isAdmin = hasRole(currentUser, "ROLE_ADMIN");

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change another user's username");
        }

        targetUser.setUsername(changeUsernameDTO.newUsername());
        customUserRepository.save(targetUser);

        logger.info("Username changed from {} to {} by {}",
                changeUsernameDTO.username(),
                changeUsernameDTO.newUsername(),
                currentUser.getUsername());
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO) {

        CustomUser currentUser = getCurrentUserOrThrow();


        CustomUser targetUser = customUserRepository.findByUsername(changePasswordDTO.username());
        if (targetUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


        boolean isAdmin = hasRole(currentUser, "ROLE_ADMIN");


        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change another user's password");
        }

        targetUser.setLastPasswordChange(Instant.now());
        targetUser.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        customUserRepository.save(targetUser);

        logger.info("Password changed for user {} by {}",
                targetUser.getUsername(),
                currentUser.getUsername());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        CustomUser userToDelete = customUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CustomUser currentUser = getCurrentUserOrThrow();

        // hindra att ta bort sig själv
        if (currentUser.getId().equals(userToDelete.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself");
        }

        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete users");
        }

        if (hasRole(userToDelete, "ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete an admin user");
        }

        logger.info("Successfully deleted user with id: {} by admin: {}", userToDelete.getId(), currentUser.getUsername());
        customUserRepository.delete(userToDelete);
    }

    private CustomUser getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        CustomUser currentUser = customUserRepository.findByUsername(auth.getName());
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found");
        }
        return currentUser;
    }

    private boolean hasRole(CustomUser user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

}
