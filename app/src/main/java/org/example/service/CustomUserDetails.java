package org.example.service;

import org.example.entities.UserInfo;
import org.example.entities.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UserInfo userInfo;

    public CustomUserDetails(UserInfo userInfo){ // Constructor that takes UserInfo

        this.userInfo=userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword(){
        return userInfo.getPassword();
    }
    @Override
    public String getUsername(){
        return userInfo.getEmail();
    }
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }
    @Override
    public boolean isEnabled(){
        return true;
    }
    public String getDisplayName() {
        // ✅ Use this wherever you need the nickname/handle
        return userInfo.getUsername();
    }

}
