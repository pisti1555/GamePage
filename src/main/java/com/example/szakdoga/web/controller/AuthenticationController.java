package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.user.User;
import com.example.szakdoga.service.AuthenticationService;
import com.example.szakdoga.web.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "authenticate/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationDto") RegistrationDto dto) {
        User user = authenticationService.register(dto);
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
