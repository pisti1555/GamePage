package project.gamepage.web.controller.auth;

import project.gamepage.data.model.user.User;
import project.gamepage.service.UserServiceImpl;
import project.gamepage.web.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public AuthenticationController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }


    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "authenticate/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationDto") RegistrationDto dto) {
        User user = userServiceImpl.register(dto);
        if (user == null) {
            return "redirect:/register?error";
        }
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "authenticate/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username")String username, @RequestParam("password")String password) {
        return "redirect:/";
    }
}
