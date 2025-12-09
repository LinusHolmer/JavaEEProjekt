package com.example.JavaEE.jwt;

import com.example.JavaEE.repository.CustomUserRepository;
import com.example.JavaEE.service.MyUserDetailsService;
import com.example.JavaEE.service.TokenService;
import com.mongodb.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final MyUserDetailsService myUserDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final CustomUserRepository customUserRepository;

    @Autowired
    public JwtAuthFilter(TokenService tokenService, MyUserDetailsService myUserDetailsService, CustomUserRepository customUserRepository) {
        this.tokenService = tokenService;
        this.myUserDetailsService = myUserDetailsService;
        this.customUserRepository = customUserRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        logger.info("Incoming request: {} {}" , request.getMethod(), request.getRequestURI());

        String token = extractJwtFromRequest(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenService.validateJwtToken(token)) {
            String username = tokenService.getUsernameFromJwtToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                if (userDetails != null && userDetails.isEnabled()) {

                    Instant tokenLastPasswordChange = tokenService.getLastPasswordChangeFromToken(token);
                    Instant userLastPasswordChange = customUserRepository.findByUsername(username).getLastPasswordChange();

                    if (tokenLastPasswordChange.isBefore(userLastPasswordChange)) {
                        logger.warn("JWT token is expired due to password change for user '{}'", username);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired due to password change");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Authenticated (DB verified) user '{}'", username);
                } else {
                    logger.warn("User '{}' not found or disabled", username);
                }
            }
        } else {
            logger.warn("Invalid JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
            return;
        }

        filterChain.doFilter(request, response);
        logger.info("Outgoing response: status={}" , response.getStatus());
        logger.debug("---- JwtAuthenticationFilter END ----");
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
