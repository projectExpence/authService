package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.model.UserInfoDto;
import org.example.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("auth/v1/signup")
    public ResponseEntity Signup(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean userAlreadyExists = userDetailsServiceImplement.sighupUser(userInfoDto);
            if(Boolean.FALSE.equals(userAlreadyExists)){
                return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getEmail());
            String JwtToken = jwtService.GenerateToken(userInfoDto.getEmail());
            return new ResponseEntity<>(JwtResponseDto.builder()
                    .accessToken(JwtToken).token(refreshToken.getToken()).build(),HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Error during signup: " +
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
