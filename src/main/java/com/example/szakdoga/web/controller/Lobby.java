package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/lobby")
public class Lobby {
    GameService service;
    @Autowired
    public Lobby(GameService service) {
        this.service = service;
    }

    @GetMapping
    public String getLobby(Model model, Principal principal) {
        String username = principal.getName();
        PvP game = service.getGame(username);
        model.addAttribute("username", username);
        model.addAttribute("game", game);
        model.addAttribute("string", game.getGame());
        model.addAttribute("invites", service.getInvites(username));
        model.addAttribute("player1", game.getUser1());
        model.addAttribute("player2", game.getUser2());
        return "lobby";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        service.inviteFriend(principal.getName(), friendUsername);
        return "redirect:/lobby";
    }

    @PostMapping("/join")
    public String joinLobby(@RequestParam("inviterName") String inviterName, Principal principal, Model model) {
        PvP game = service.joinLobby(inviterName, principal.getName());
        if (game != null) {
            model.addAttribute("string", game.getGame());
            return "redirect:/lobby";
        } else {
            return "redirect:/lobby?error=joinFailed";
        }
    }

    @PostMapping("/updateGameCode")
    public String updateGameCode(@RequestParam("newGameCode") String newGameCode, Model model, Principal principal) {;
        service.changeString(principal.getName(), newGameCode);

        return "redirect:/lobby";
    }
}