package com.mtran.mvc.application.config.webconfig;


import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final UserEntity userEntity;
    private final List<String> roles;

    public CustomUserDetails(UserEntity userEntity, List<String> roles) {
        this.userEntity = userEntity;
        this.roles = roles;
    }

    public UserEntity getUser() {
        return userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !userEntity.isDeleted();
    }

    public List<String> getRoles() {
        return roles;
    }


}
