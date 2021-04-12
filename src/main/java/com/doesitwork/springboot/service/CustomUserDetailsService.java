package com.doesitwork.springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.doesitwork.springboot.domain.enums.UserType;
import com.doesitwork.springboot.logging.Operation;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String DEFAULT_ROLE = UserType.USER.value();

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Operation operation = Operation.operation("loadUserByUsername").with("username", username).started(this);

        operation.wasSuccessful().log();
        return new org.springframework.security.core.userdetails.User("replace_username", "replace_password", getAuthority());
    }

    // TODO: Add User object
    private List<SimpleGrantedAuthority> getAuthority() {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(DEFAULT_ROLE));

        return authorities;
    }

}
