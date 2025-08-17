package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.example.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImplement userDetailsServiceImplement; // to check the email

    @Autowired
    private UserRepository userRepository;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> Signup(@RequestBody UserInfoDto userInfoDto) {
        try {
            UserDetailsServiceImplement.SignupResult result = userDetailsServiceImplement.signupUser(userInfoDto);
            switch (result) {
                case INVALID_EMAIL:
                    return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
                case INVALID_PASSWORD:
                    return new ResponseEntity<>("Password must be at least 8 characters and contain a special character", HttpStatus.BAD_REQUEST);
                case USER_EXISTS:
                    return new ResponseEntity<>("Username or Email already in use. Use a different one", HttpStatus.BAD_REQUEST);
                case SUCCESS:
                    UserInfo savedUser = userRepository.findByEmail(userInfoDto.getEmail())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found after signup"));
                    RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(savedUser);
                    String accessToken = jwtService.GenerateToken(userInfoDto.getEmail());

                    return new ResponseEntity<>(JwtResponseDto.builder() // data went to postman
                            .accessToken(accessToken).token(refreshToken.getToken()).build(), HttpStatus.OK);
                default:
                    return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error during signup: " +
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/admin/signup")
    public ResponseEntity<?> AdminSignup(@RequestBody UserInfoDto userInfoDto) {
        try {
            UserDetailsServiceImplement.SignupResult result = userDetailsServiceImplement.signupAdmin(userInfoDto);
            switch (result) {
                case INVALID_EMAIL:
                    return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
                case INVALID_PASSWORD:
                    return new ResponseEntity<>("Password must be at least 8 characters and contain a special character", HttpStatus.BAD_REQUEST);
                case USER_EXISTS:
                    return new ResponseEntity<>("Username or Email already in use", HttpStatus.BAD_REQUEST);
                case SUCCESS:
                    UserInfo saveUser = userRepository.findByEmail(userInfoDto.getEmail()).
                            orElseThrow(() -> new UsernameNotFoundException("User not found after signup"));
                    RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(saveUser);
                    String accessToken = jwtService.GenerateToken(userInfoDto.getEmail());
                    return new ResponseEntity<>(JwtResponseDto.builder()
                            .accessToken(accessToken).token(refreshToken.getToken()).build(), HttpStatus.OK);

                default:
                    return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error during signup: " +
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}