package project.gamepage.service;

import project.gamepage.data.model.game.stats.FitwStats;
import project.gamepage.data.model.game.stats.TicTacToeStats;
import project.gamepage.data.model.user.Role;
import project.gamepage.data.model.user.User;
import project.gamepage.data.repository.FitwRepository;
import project.gamepage.data.repository.TicTacToeRepository;
import project.gamepage.data.repository.UserRepository;
import project.gamepage.web.dto.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FitwRepository fitwRepository;
    private final TicTacToeRepository ticTacToeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FitwRepository fitwRepository, TicTacToeRepository ticTacToeRepository) {
        this.userRepository = userRepository;
        this.fitwRepository = fitwRepository;
        this.ticTacToeRepository = ticTacToeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String validateDto(UserDto dto) {
        StringBuilder builder = new StringBuilder();
        boolean isUsernameAvailable = false;
        boolean isEmailAvailable = false;
        boolean isUsernameValid = !dto.getUsername().contains(" ");
        boolean isPasswordValid = false;
        boolean passwordsMatch = dto.getPassword().equals(dto.getConfirmPassword());
        boolean isEmailValid;

        if (userRepository.findByUsername(dto.getUsername()) == null) isUsernameAvailable = true;
        if (userRepository.findByEmail(dto.getEmail()) == null) isEmailAvailable = true;

        if (dto.getPassword().length() >= 6 && dto.getPassword().length() <= 30 &&
                dto.getPassword().matches(".*[a-zA-Z].*") &&
                dto.getPassword().matches(".*[0-9].*")) isPasswordValid = true;

        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(dto.getEmail());
        isEmailValid = emailMatcher.matches();

        if (!isUsernameAvailable) builder.append("&usernameTaken");
        if (!isEmailAvailable) builder.append("&emailTaken");
        if (!isUsernameValid) builder.append("&usernameInvalid");
        if (!isEmailValid) builder.append("&emailInvalid");
        if (!isPasswordValid) builder.append("&passwordInvalid");
        if (!passwordsMatch) builder.append("&passwordConfirmationError");

        return builder.toString();
    }

    @Override
    public User editProfile(User user, UserDto dto) {
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            if (!user.getUsername().equals(dto.getUsername())) {
                if (
                        userRepository.findByUsername(dto.getUsername()) == null
                                && !dto.getUsername().contains(" ")
                ) {
                    user.setUsername(dto.getUsername());
                    update(user);
                    return user;
                }
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!user.getEmail().equals(dto.getEmail())) {
                String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
                Pattern emailPattern = Pattern.compile(emailRegex);
                Matcher emailMatcher = emailPattern.matcher(dto.getEmail());
                if (
                        userRepository.findByEmail(dto.getEmail()) == null
                                && emailMatcher.matches()
                ) {
                    user.setEmail(dto.getEmail());
                    update(user);
                    return user;
                }
            }
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!user.getPassword().equals(dto.getPassword())) {
                if (
                        dto.getPassword().equals(dto.getConfirmPassword()) &&
                                dto.getPassword().length() >= 6 && dto.getPassword().length() <= 30 &&
                                dto.getPassword().matches(".*[a-zA-Z].*") &&
                                dto.getPassword().matches(".*[0-9].*")
                ) {
                    String password = passwordEncoder.encode(dto.getPassword());
                    user.setPassword(password);
                    update(user);
                    return user;
                }
            }
        }

        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            if (!user.getFirstName().equals(dto.getFirstName())) {
                user.setFirstName(dto.getFirstName());
                update(user);
                return user;
            }
        }

        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            if (!user.getLastName().equals(dto.getLastName())) {
                user.setLastName(dto.getLastName());
                update(user);
                return user;
            }
        }

        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            if (!user.getDescription().equals(dto.getDescription())) {
                user.setDescription(dto.getDescription());
                update(user);
                return user;
            }
        }

        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            if (!user.getAvatar().equals(dto.getAvatar())) {
                user.setAvatar(dto.getAvatar());
                update(user);
                return user;
            }
        }

        return null;
    }

    @Override
    public String register(UserDto dto) {
        String response = validateDto(dto);
        if (response.isEmpty()) {
            String password = passwordEncoder.encode(dto.getPassword());
            User user = new User(dto.getUsername(), dto.getAvatar(), dto.getEmail(), password,
                    dto.getFirstName(), dto.getLastName(),
                    List.of(new Role("ROLE_USER")), 0, 0, 0);
            userRepository.save(user);
        }
        return response;
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
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.searchByUsername(query);
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