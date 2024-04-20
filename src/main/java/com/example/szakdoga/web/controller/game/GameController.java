package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.game.Game;
import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

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
    public String getGamePage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/gameMenu";
    }

    @PostMapping("/updateGameCode")
    public String updateGameCode(@RequestParam("newGameCode") String newGameCode, Model model, Principal principal) {;
        service.changeString(principal.getName(), newGameCode);

        return "redirect:/game/pvp";
    }

    @GetMapping("/pvp")
    public String getGamePage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        Game game = service.getGame(username);
        if (game != null) {
            model.addAttribute("string", game.getString());
            System.out.println("User: " + principal.getName());
            System.out.println("Board: " + game.getBoard());
            return "game/spiderweb_pvp";
        } else {
            return "redirect:/lobby?error=noGameFound";
        }
    }

    @GetMapping("/pvs")
    public String pvs(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/spiderweb_pvs";
    }

    @GetMapping("/pvf")
    public String pvf(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/spiderweb_pvf";
    }


}
