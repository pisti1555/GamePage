package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String getGamePage(Model model, Principal principal, RedirectAttributes attributes) {
        PvP game = service.getGame((String) attributes.getAttribute("id"));
        model.addAttribute("username", principal.getName());
        model.addAttribute("game", game);
        model.addAttribute("string", game.getGame());
        return "game";
    }


    @GetMapping("/string")
    public String playGame(Model model, Principal principal) {
        return "string";
    }

    @PostMapping("/updateGameCode")
    public String updateGameCode(@RequestParam("newGameCode") String newGameCode, Model model, Principal principal) {;
        service.changeString(principal.getName(), newGameCode);

        return "redirect:/play";
    }
}
