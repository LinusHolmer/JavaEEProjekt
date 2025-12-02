package com.example.JavaEE.service;


import com.example.JavaEE.dto.RegisterRequest;
import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;


@Service
public class CustomUserService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CustomUserService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Kollar först om det finns en användare med samma namn i databasen

    public CustomUser registerUser(RegisterRequest registerRequest) {
        if (customUserRepository.findByUsername(registerRequest.username()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        //Skapar ny ChatUser, sätter lösenord och username och hashar lösenord innan det sparas med .encode
        CustomUser customUser = new CustomUser();
        customUser.setUsername(registerRequest.username());
        customUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        customUser.setRoles(Set.of("ROLE_USER"));

        try {
            //sparar användaren
            return customUserRepository.save(customUser);

            //Dubbelkollar att 2 användare inte skrivit in samma namn samtidigt
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
    }

}


