package com.techinitializer.spring_security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techinitializer.spring_security.domain.RefreshToken;
import com.techinitializer.spring_security.domain.User;
import com.techinitializer.spring_security.dto.AuthResponse;
import com.techinitializer.spring_security.dto.ErrorResponse;
import com.techinitializer.spring_security.dto.LoginRequest;
import com.techinitializer.spring_security.dto.RefreshTokenRequest;
import com.techinitializer.spring_security.repository.RefreshTokenRepository;
import com.techinitializer.spring_security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            User userDetails = (User) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(authentication);

            // Generate and persist refresh token
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            RefreshToken refreshTokenEntity = new RefreshToken(
                    refreshToken,
                    Date.from(Instant.now().plusMillis(jwtUtil.getRefreshExpirationInMs())),
                    userDetails
            );

            refreshTokenRepository.save(refreshTokenEntity);

            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
        } catch (Exception e) {
            // Handle any authentication or other exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid credentials or login error"));
        }
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {

            String refreshToken = refreshTokenRequest.refreshToken();

            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(refreshToken);

            if (refreshTokenOptional.isEmpty() || refreshTokenOptional.get().getExpiryDate().before(new Date())
                    || refreshTokenOptional.get().isRevoked()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid refresh token"));
            }

            RefreshToken refreshTokenEntity = refreshTokenOptional.get();
            UserDetails userDetails = refreshTokenEntity.getUser();

            String newJwt = jwtUtil.generateToken(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );

            // Optionally generate and update a new refresh token
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            refreshTokenEntity.setToken(newRefreshToken);
            refreshTokenEntity.setExpiryDate(Date.from(Instant.now().plusMillis(jwtUtil.getRefreshExpirationInMs())));
            refreshTokenRepository.save(refreshTokenEntity);

            return ResponseEntity.ok(new AuthResponse(newJwt, newRefreshToken));

        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid refresh token"));
        }
    }

    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtUtil.getUsernameFromToken(jwt);

                List<RefreshToken> refreshTokens = refreshTokenRepository.findByUser_Username(username);
                for (RefreshToken token : refreshTokens) {
                    token.setRevoked(true);
                }
                refreshTokenRepository.saveAll(refreshTokens);
            }

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Logout failed."));
        }
    }
}
