package project.gamepage.service;

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
    public String editProfile(User user, UserDto dto) {
        StringBuilder builder = new StringBuilder();

        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            if (!dto.getAvatar().equals(user.getAvatar())) {
                user.setAvatar(dto.getAvatar());
                update(user);
            }
        }

        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            if (dto.getDescription().length() <= 100) {
                user.setDescription(dto.getDescription());
                update(user);
            } else builder.append("&descriptionTooLong");
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!dto.getEmail().equals(user.getEmail())) {
                String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
                Pattern emailPattern = Pattern.compile(emailRegex);
                Matcher emailMatcher = emailPattern.matcher(dto.getEmail());
                if (userRepository.findByEmail(dto.getEmail()) == null) {
                    if (emailMatcher.matches()) {
                        user.setEmail(dto.getEmail());
                        update(user);
                    } else builder.append("&emailInvalid");
                } else builder.append("&emailTaken");
            } else builder.append("&emailMatchesPrevious");
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!dto.getPassword().equals(user.getPassword())) {
                if (dto.getPassword().equals(dto.getConfirmPassword())) {
                    if (
                            dto.getPassword().length() >= 6 && dto.getPassword().length() <= 30 &&
                                    dto.getPassword().matches(".*[a-zA-Z].*") &&
                                    dto.getPassword().matches(".*[0-9].*")
                    ) {
                        String password = passwordEncoder.encode(dto.getPassword());
                        user.setPassword(password);
                        update(user);
                    } else builder.append("&passwordInvalid");
                } else builder.append("&passwordConfirmationError");
            } else builder.append("&passwordMatchesPrevious");
        }

        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            if (!dto.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(dto.getFirstName());
                update(user);
            } else builder.append("&firstnameMatchesPrevious");
        }

        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            if (!dto.getLastName().equals(user.getLastName())) {
                user.setLastName(dto.getLastName());
                update(user);
            } else builder.append("&lastnameMatchesPrevious");
        }

        return builder.toString();
    }

    @Override
    public String register(UserDto dto) {
        String response = validateDto(dto);
        if (response.isEmpty()) {
            String password = passwordEncoder.encode(dto.getPassword());
            User user = new User(dto.getUsername(), dto.getAvatar(), dto.getEmail(), password,
                    dto.getFirstName(), dto.getLastName(),
                    List.of(new Role("ROLE_USER")));
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