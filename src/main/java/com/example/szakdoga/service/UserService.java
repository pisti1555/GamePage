package com.example.szakdoga.service;

import com.example.szakdoga.web.dto.RegistrationDto;
import com.example.szakdoga.data.model.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User register(RegistrationDto dto);
    List<User> findAll();
    User findByUsername(String username);
    void update(User user);
}
