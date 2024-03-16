package com.example.szakdoga.service;

import com.example.szakdoga.web.dto.RegistrationDto;
import com.example.szakdoga.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(RegistrationDto dto);
}
