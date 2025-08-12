package org.example.controller;

import org.example.entities.UserInfo;
import org.example.repository.UserRepository;
import org.example.service.GoogleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    @Autowired
    private UserRepository userRepository;

    private final GoogleUserService googleUserService;
    public DashboardController(GoogleUserService googleUserService) {
        this.googleUserService = googleUserService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserInfo> getLoggedInUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String email = principal.getAttribute("email");

        // The user is ALREADY in your database. Just find them.
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok) // If found, return user with 200 OK
                .orElse(ResponseEntity.status(404).build()); // If not found, return 404
    }

}
