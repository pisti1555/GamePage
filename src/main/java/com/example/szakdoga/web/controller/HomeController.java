package com.example.szakdoga.web.controller;

import com.example.szakdoga.service.InvitationService;
import com.example.szakdoga.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class HomeController {
    UserService userService;
    InvitationService invitationService;

    @Autowired
    public HomeController(UserService userService, InvitationService invitationService) {
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, Principal principal, HttpSession session) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));
        return "index";
    }

    @GetMapping("/scoreboard")
    public String getScoreboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));
        return "player/scoreboard";
    }

}
