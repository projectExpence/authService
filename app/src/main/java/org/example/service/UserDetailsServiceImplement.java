package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entities.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImplement implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("Could not find user"));
        return new CustomUserDetails(user);
    }

    public UserInfo checkUserAlreadyExists(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUserName()).orElse(null);
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

    public Boolean sighupUser(UserInfoDto userInfoDto){
        if(!isValidEmail(userInfoDto.getEmail()) || !isValidPassword(userInfoDto.getPassword())){
            return false;
        }
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkUserAlreadyExists(userInfoDto))){
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId,userInfoDto.getUserName(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),new HashSet<>()));
        return true;
    }
}
