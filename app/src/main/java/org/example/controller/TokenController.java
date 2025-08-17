package org.example.controller;

import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.repository.UserRepository;
import org.example.request.AuthRequestDto;
import org.example.request.RefreshTokenRequestDto;
import org.example.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    // This class can be used to handle token-related operations
    // such as refreshing tokens, validating tokens, etc.

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired RefreshTokenService refreshTokenService;

    @Autowired JwtService jwtService;

    @Autowired private UserRepository userRepository;


    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto){
        try{
            Authentication authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(authRequestDto.getEmail(),authRequestDto.getPassword()));
            if (authentication.isAuthenticated()) {
                // Fetch user from DB
                UserInfo user = userRepository.findByEmail(authRequestDto.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Create or rotate refresh token
                RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user);

                // Generate access token
                String accessToken = jwtService.GenerateToken(authRequestDto.getEmail());

                return new ResponseEntity<>(JwtResponseDto.builder()
                        .accessToken(accessToken)
                        .token(refreshToken.getToken())
                        .build(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error: Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error during authentication: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/admin/login")
    public ResponseEntity AdminLoginAndGetToken(@RequestBody AuthRequestDto authRequestDto){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(),authRequestDto.getPassword())
            );
            if(authentication.isAuthenticated()) {
                UserInfo user = userRepository.findByEmail(authRequestDto.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getRoleName().name().equals("ADMIN"));

                if (!isAdmin) {
                    return new ResponseEntity<>("Access denied: Not an admin", HttpStatus.FORBIDDEN);
                }

                RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user);

                String accessToken = jwtService.GenerateToken(authRequestDto.getEmail());

                return new ResponseEntity<>(JwtResponseDto.builder()
                        .accessToken(accessToken).token(refreshToken.getToken()).build(),HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error: Invalid credentials",HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error during authentication: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(userInfo);
                    String accessToken = jwtService.GenerateToken(userInfo.getEmail());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(newRefreshToken.getToken()) // ✅ New rotated refresh token
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not on DB"));
    }

}
