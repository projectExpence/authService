package org.example.service;

import org.example.entities.Provider;
import org.example.entities.UserInfo;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
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
            user = new UserInfo();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setUsername(email);
            user.setFirstName(oauth2User.getAttribute("given_name"));
            user.setLastName(oauth2User.getAttribute("family_name"));
            user.setProfilePictureUrl(oauth2User.getAttribute("picture"));
            user.setProvider(Provider.GOOGLE);
            user.setPassword(null);
        }

        userRepository.save(user);

        return oauth2User;
    }
}
