package org.example.service;

import org.example.entities.Provider;
import org.example.entities.Roles;
import org.example.entities.UserInfo;
import org.example.entities.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class GoogleUserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String username=email.substring(0,email.indexOf("@"));
        Optional<UserInfo> userOptional = userRepository.findByEmail(email);

        UserInfo user;
        if (userOptional.isPresent()) {
            // User exists, update their details
            user = userOptional.get();
            user.setFirstName(oauth2User.getAttribute("given_name"));
            user.setLastName(oauth2User.getAttribute("family_name"));
            user.setProfilePictureUrl(oauth2User.getAttribute("picture"));
            user.setProvider(Provider.GOOGLE);
        } else {
            // New user, create a new record

            UserRole userRole = userRoleRepository.findByRoleName(Roles.USER)
                    .orElseThrow(()->new RuntimeException("User role not found"));
            user = new UserInfo();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstName(oauth2User.getAttribute("given_name"));
            user.setLastName(oauth2User.getAttribute("family_name"));
            user.setProfilePictureUrl(oauth2User.getAttribute("picture"));
            user.setProvider(Provider.GOOGLE);
            user.setPassword(null);
            user.setRoles(Set.of(userRole));
        }

        userRepository.save(user);

        return oauth2User;
    }
}
