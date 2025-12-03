package com.example.JavaEE.service;

import com.example.JavaEE.dto.ChangePasswordDTO;
import com.example.JavaEE.dto.ChangeUsernameDTO;
import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
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

}
