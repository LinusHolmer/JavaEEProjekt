package com.example.JavaEE.service;

import com.example.JavaEE.jwt.JwtAuthFilter;
import com.example.JavaEE.model.CustomUser;

import com.example.JavaEE.repository.CustomUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


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

    // promotar till admin
    public CustomUser promoteToAdmin(String userId) {
        // Försöker hämta användaren från databasen med ID
        // Om ingen finns kastar man ett 404 not found fel
        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        // Hämta roller från användaren
        // Om användaren inte har några roller än skapar man en tom lista
        Set<String> roles = user.getRoles() == null
                ? new HashSet<>()
                : new HashSet<>(user.getRoles());

        // lägg till admin rollen.
        // Spring använder "ROLE_ADMIN" som standardnamn för admin
        roles.add("ROLE_ADMIN");

        // uppdaterar användarens roll
        user.setRoles(roles);

        // sparar användaren i databasen och returnar den uppdaterade versionen
        logger.info("Successfully promoted user: {} to admin", user.getUsername());
        return customUserRepository.save(user);
    }
  
   public void changeUsername(ChangeUsernameDTO changeUsernameDTO) {
        CustomUser customUser = customUserRepository.findByUsername(changeUsernameDTO.username());
        if(customUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        customUser.setUsername(changeUsernameDTO.newUsername());
        logger.info("Changed username {} to username {}", changeUsernameDTO.username(), changeUsernameDTO.newUsername());
        customUserRepository.save(customUser);
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        CustomUser customUser = customUserRepository.findByUsername(changePasswordDTO.username());
        if(customUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        customUser.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        logger.info("Changed password for user: {}", changePasswordDTO.username());
        customUserRepository.save(customUser);
    }
  

    // tar bort användare
    public void deleteUser(String userId) {

        // Hitta användaren som ska raderas och m användaren inte finns blir det 404 not found.
        CustomUser userToDelete = customUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // hindrar att ta bort sig själv
        // Hämta användaren som är inloggad just nu (auth kommer från Jwt)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String currentUsername = auth.getName();
            CustomUser currentUser = customUserRepository.findByUsername(currentUsername);
            if (currentUser != null && currentUser.getId().equals(userToDelete.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself");
            }
        }
        logger.info("Successfully deleted user with id: {}", userToDelete.getId());
        customUserRepository.delete(userToDelete);
    }
}
