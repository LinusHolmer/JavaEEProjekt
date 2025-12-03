package com.example.JavaEE.service;

import com.example.JavaEE.model.CustomUser;

import com.example.JavaEE.repository.CustomUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;


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
        return customUserRepository.save(user);
    }
  
   public void changeUsername(ChangeUsernameDTO changeUsernameDTO) {
        CustomUser customUser = customUserRepository.findByUsername(changeUsernameDTO.username());
        customUser.setUsername(changeUsernameDTO.newUsername());
        customUserRepository.save(customUser);
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        CustomUser customUser = customUserRepository.findByUsername(changePasswordDTO.username());
        customUser.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
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

        customUserRepository.delete(userToDelete);
    }
}
