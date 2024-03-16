package com.example.szakdoga.web.controller;

import com.example.szakdoga.model.User;
import com.example.szakdoga.service.UserService;
import com.example.szakdoga.web.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public RegistrationDto registrationDto() {
        return new RegistrationDto();
    }

    @GetMapping
    public String getRegisterPage() {
        return "register";
    }

    @PostMapping
    public String register(@ModelAttribute("user") RegistrationDto dto) {
        User user = userService.save(dto);
        if (user == null) {
            return "redirect:/register?error";
        }
        return "redirect:/register?success";
    }
}
