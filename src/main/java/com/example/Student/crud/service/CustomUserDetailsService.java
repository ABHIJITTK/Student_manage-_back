package com.example.Student.crud.service;

import com.example.Student.crud.model.UserEntity;
import com.example.Student.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("User not found !")
        );

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(
                                role -> new SimpleGrantedAuthority(role.getName())
                        ).collect(Collectors.toList())
        );
    }
}