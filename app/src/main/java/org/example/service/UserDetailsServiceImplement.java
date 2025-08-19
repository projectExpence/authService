package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entities.Provider;
import org.example.entities.Roles;
import org.example.entities.UserInfo;
import org.example.entities.UserRole;
import org.example.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.example.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImplement implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserInfoProducer userInfoProducer;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("Could not find user"));
        return new CustomUserDetails(user);
    }

    public boolean checkUserAlreadyExists(UserInfoDto userInfoDto) {
        return userRepository.findByEmail(userInfoDto.getEmail()).isPresent()
                || userRepository.findByUsername(userInfoDto.getUsername()).isPresent();
    }
    public UserInfo checkUserByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }


    public Boolean isValidEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email !=null && email.matches(emailRegex);
    }

    public Boolean isValidPassword(String password){
        if(password == null || password.length() < 8){
            return false;
        }
        String specialCharRegex = ".*[!@#$%^&*(),.?\":{}|<>].*";
        return password.matches(specialCharRegex);
    }

    public enum SignupResult {
        SUCCESS,
        INVALID_EMAIL,
        INVALID_PASSWORD,
        USER_EXISTS
    }

    public SignupResult signupUser(UserInfoDto userInfoDto){
        if(!isValidEmail(userInfoDto.getEmail())){
            return SignupResult.INVALID_EMAIL;
        }
        if(!isValidPassword(userInfoDto.getPassword())){
            return SignupResult.INVALID_PASSWORD;
        }
        if (checkUserAlreadyExists(userInfoDto)) {  // Now returns boolean
            return SignupResult.USER_EXISTS;
        }
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        UserRole userRole = userRoleRepository.findByRoleName(Roles.USER)
                .orElseThrow(()-> new RuntimeException("User role not found in Database"));


        String userId = UUID.randomUUID().toString();
        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        userRepository.save(new UserInfo(
                userId,
                userInfoDto.getFirstName(),
                userInfoDto.getLastName(),
                userInfoDto.getProfilePictureUrl(),
                Provider.LOCAL,
                userInfoDto.getUsername(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),roles
                ));
        userInfoProducer.sentEventToKafka(userInfoDto); // data send to kafka
        return SignupResult.SUCCESS;
    }





    public SignupResult signupAdmin(UserInfoDto userInfoDto){
        if(!isValidEmail(userInfoDto.getEmail())){
            return SignupResult.INVALID_EMAIL;
        }
        if(!isValidPassword(userInfoDto.getPassword())){
            return SignupResult.INVALID_PASSWORD;
        }
        if (checkUserAlreadyExists(userInfoDto)) {  // Now returns boolean
            return SignupResult.USER_EXISTS;
        }
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        UserRole userRole = userRoleRepository.findByRoleName(Roles.ADMIN)
                .orElseThrow(()-> new RuntimeException("User role not found in Database"));

        String userId = UUID.randomUUID().toString();
        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        userRepository.save(new UserInfo(
                userId,
                userInfoDto.getFirstName(),
                userInfoDto.getLastName(),
                userInfoDto.getProfilePictureUrl(),
                Provider.LOCAL,
                userInfoDto.getUsername(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),
                roles

        ));
        userInfoProducer.sentEventToKafka(userInfoDto);
        return SignupResult.SUCCESS;
    }
}
