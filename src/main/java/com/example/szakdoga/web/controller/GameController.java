package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/game")
public class GameController {
    GameService service;
    SimpMessagingTemplate template;

    @Autowired
    public GameController(GameService service, SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @GetMapping
    public String getGamePage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        PvP game = service.getGame(username);
        if (game != null) {
            model.addAttribute("string", game.getString());
            return "game";
        } else {
            return "redirect:/lobby?error=noGameFound";
        }
    }

    @PostMapping("/updateGameCode")
    public String updateGameCode(@RequestParam("newGameCode") String newGameCode, Model model, Principal principal) {;
        service.changeString(principal.getName(), newGameCode);

        return "redirect:/game";
    }

}
