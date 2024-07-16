package project.gamepage.service;

import project.gamepage.web.dto.UserDto;
import project.gamepage.data.model.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    String validateDto(UserDto dto);
    String register(UserDto dto);
    List<User> findAll();
    User findByUsername(String username);
    User editProfile(User user, UserDto dto);
    void update(User user);
    List<User> searchUsers(String query);
}
