package com.example.szakdoga.service;

import com.example.szakdoga.data.model.Role;
import com.example.szakdoga.data.model.User;
import com.example.szakdoga.data.repository.UserRepository;
import com.example.szakdoga.web.dto.RegistrationDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AuthenticationService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User register(RegistrationDto dto) {
        String password = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getUsername(), dto.getEmail(), password,
                dto.getFirstName(), dto.getLastName(),
                List.of(new Role("ROLE_USER")), 0, 0, 0);
        if (userRepository.findByUsername(dto.getUsername()) != null
                || userRepository.findByEmail(dto.getEmail()) != null
        ) {
            return null;
        } else return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }
}