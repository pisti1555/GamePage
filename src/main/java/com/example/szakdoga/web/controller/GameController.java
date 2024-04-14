package com.example.szakdoga.web.controller;

import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/play")
public class GameController {
    GameService service;
    @Autowired
    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping
    public String getGamePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "spiderweb";
    }

    @GetMapping("/string")
    public String playGame(Model model, Principal principal) {
        return "string";
    }


}
