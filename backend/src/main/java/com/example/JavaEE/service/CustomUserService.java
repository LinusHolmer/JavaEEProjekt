package com.example.JavaEE.service;


import com.example.JavaEE.dto.CustomUserDTO;
import com.example.JavaEE.dto.RegisterRequest;
import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;


@Service
public class CustomUserService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserService.class);


    @Autowired
    public CustomUserService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder, TokenService tokenService, AuthenticationManager authenticationManager) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    //Kollar först om det finns en användare med samma namn i databasen

    public void registerUser(RegisterRequest registerRequest) {
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
            logger.info("Successfully created user: {}", customUser.getUsername());
            customUserRepository.save(customUser);


            //Dubbelkollar att 2 användare inte skrivit in samma namn samtidigt
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
    }

    public ResponseCookie loginUser(RegisterRequest registerRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.username(),
                        registerRequest.password()
                )
        );

        String token = tokenService.generateToken(auth);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // should be true if using https
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Strict")
                .build();
        logger.info("Successfully logged in user: {}", registerRequest.username());
        return cookie;

    }

    public ResponseCookie logoutUser() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // should be true if using https, but did work?
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        //vet inte hur jag ska få in username, Authentication auth blev null
        logger.info("Successfully logged out user");
        return cookie;
    }

    public Set<String> checkRoles(String authHeader){
        String jwt = authHeader.replace("Bearer ", "");

        //logger för detta känns onödigt
        Set<String> roles = tokenService.getRolesFromJwtToken(jwt);
        return roles;

    }

    public List<CustomUserDTO> getUsers() {

        List<CustomUserDTO> customUsers = customUserRepository.findAllBy()
                .stream()
                .map(customUser -> new CustomUserDTO(customUser.getUsername(), customUser.getRoles().toString()))
                .toList();

        return customUsers;
    }
}


