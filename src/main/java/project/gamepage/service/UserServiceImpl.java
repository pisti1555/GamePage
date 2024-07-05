package project.gamepage.service;

import project.gamepage.data.model.user.Role;
import project.gamepage.data.model.user.User;
import project.gamepage.data.repository.UserRepository;
import project.gamepage.web.dto.RegistrationDto;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String register(RegistrationDto dto) {
        StringBuilder builder = new StringBuilder();
        boolean isUsernameAvailable = false;
        boolean isEmailAvailable = false;
        boolean isUsernameValid = !dto.getUsername().contains(" ");
        boolean isPasswordValid = false;
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

        if (builder.isEmpty()) {
            String password = passwordEncoder.encode(dto.getPassword());
            User user = new User(dto.getUsername(), dto.getEmail(), password,
                    dto.getFirstName(), dto.getLastName(),
                    List.of(new Role("ROLE_USER")), 0, 0, 0);
            userRepository.save(user);
        }

        return builder.toString();
    }

    @Override
    public String validateUserData(String username, String password, String email) {
        StringBuilder builder = new StringBuilder();
        boolean isUsernameAvailable = false;
        boolean isEmailAvailable = false;
        boolean isUsernameValid = !username.contains(" ");
        boolean isPasswordValid = false;
        boolean isEmailValid;

        if (userRepository.findByUsername(username) == null) isUsernameAvailable = true;
        if (userRepository.findByEmail(email) == null) isEmailAvailable = true;

        if (password.length() >= 6 && password.length() <= 30 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*[0-9].*")) isPasswordValid = true;

        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(email);
        isEmailValid = emailMatcher.matches();

        if (!isUsernameAvailable) builder.append("&username-taken");
        if (!isEmailAvailable) builder.append("&email-taken");
        if (!isUsernameValid) builder.append("&username-invalid");
        if (!isEmailValid) builder.append("&email-invalid");
        if (!isPasswordValid) builder.append("&password-invalid");

        builder.deleteCharAt(0);

        return builder.toString();
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }
}