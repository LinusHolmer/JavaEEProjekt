package com.example.JavaEE.service;

import com.example.JavaEE.model.CustomUser;
import com.example.JavaEE.repository.CustomUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;


import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final KeyPair keyPair;


    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final CustomUserRepository customUserRepository;

    @Autowired
    public TokenService(JwtEncoder jwtEncoder, KeyPair keyPair, CustomUserRepository customUserRepository) {
        this.jwtEncoder = jwtEncoder;
        this.keyPair = keyPair;
        this.customUserRepository = customUserRepository;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        CustomUser customUser = customUserRepository.findByUsername(authentication.getName());
        Instant lastPasswordChange = customUser.getLastPasswordChange();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("lastPasswordChange", lastPasswordChange.toString())
                .build();

        logger.info("JWT generated successfully for user: {}", authentication.getName());
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getUsernameFromJwtToken (String token){
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            logger.debug("Extracted username '{}' from JWT token", username);
            return username;
        } catch (Exception e) {
            logger.warn("getUsernameFromJwtToken failed");
            return null;
        }
    }

    public boolean validateJwtToken (String authToken){
        try{
            Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(authToken);

            logger.debug("JWT validation succeeded");
            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
    public Set<String> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String scope = claims.get("scope", String.class);

        if (scope == null || scope.isBlank()) {
            logger.warn("No scope found in JWT token");
            return Set.of();
        }


        Set<String> roles = Arrays.stream(scope.split("\\s+"))
                .map(String::trim)
                .filter(s -> s.startsWith("ROLE_"))
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toSet());

        logger.debug("Extracted roles from JWT token: {}", roles);
        return roles;
    }

    public Instant getLastPasswordChangeFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String lastChange = claims.get("lastPasswordChange", String.class);
            if (lastChange == null) {
                return null;
            }

            return Instant.parse(lastChange);

        } catch (Exception e) {
            logger.warn("getLastPasswordChangeFromToken failed!");
            return null;
        }
    }

    }

