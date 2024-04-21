package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.User;
import com.example.szakdoga.data.model.game.Game;
import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.service.GameService;
import com.example.szakdoga.service.UserService;
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
    UserService userService;
    SimpMessagingTemplate template;

    @Autowired
    public GameController(GameService service, UserService userService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.template = template;
    }

    @GetMapping
    public String getGamePage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/gameMenu";
    }

    @GetMapping("/pvp")
    public String getGamePage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        Game game = service.getGame(username);
        if (game != null) {
            if (game.isReady()) {
                boolean gameOver = service.getIsGameRunning(game.getBoard());
                int whoWon = service.whoWon(game.getBoard());
                int flyStepsDone = service.getFlyStepsDone(game.getBoard());
                int spiderStepsDone = service.getSpiderStepsDone(game.getBoard());
                int totalStepsDone = flyStepsDone + spiderStepsDone;

                gameOver(username);

                model.addAttribute("gameOver", gameOver);
                model.addAttribute("whoWon", whoWon);
                model.addAttribute("flySteps", flyStepsDone);
                model.addAttribute("spiderSteps", spiderStepsDone);
                model.addAttribute("totalSteps", totalStepsDone);
                return "game/spiderweb_pvp";
            }
        }

        return "redirect:/lobby?error=noGameFound";
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

    @PostMapping("/new-game")
    public String newGame() {
        return null;
        //TODO
    }

    @PostMapping("/quit")
    public String quit() {
        return null;
        //TODO
    }

    public void gameOver(String name) {
        User user = userService.findByUsername(name);
        Game game = service.getGame(user.getUsername());
        if (!game.getBoard().isGameRunning()) {
            service.gameOver(game, user);
            userService.update(user);
        }
    }
}