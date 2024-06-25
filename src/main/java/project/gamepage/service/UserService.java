package project.gamepage.service;

import project.gamepage.web.dto.RegistrationDto;
import project.gamepage.data.model.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User register(RegistrationDto dto);
    List<User> findAll();
    User findByUsername(String username);
    void update(User user);
}
