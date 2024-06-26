package project.gamepage.web.controller.game.tic_tac_toe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;

import java.security.Principal;

@Controller
@RequestMapping("/tic-tac-toe/game")
public class GameController_TicTacToe {
    SimpMessagingTemplate template;
    GameService_TicTacToe service;
    @Autowired
    public GameController_TicTacToe(SimpMessagingTemplate template, GameService_TicTacToe service) {
        this.template = template;
        this.service = service;
    }

    @GetMapping("/ai")
    public String getGamePageAI(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/tic_tac_toe/game_page_ai";
    }

    @GetMapping("/pvp")
    public String getGamePagePvP(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/tic_tac_toe/game_page_pvp";
    }

    @GetMapping("/return-to-lobby")
    public String returnToLobby_TicTacToe(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        if (principal.getName().equals(pvp.getUser1())) {
            pvp.setUser1InGame(false);
            template.convertAndSendToUser(pvp.getUser2(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "return");
        } else {
            pvp.setUser2InGame(false);
            template.convertAndSendToUser(pvp.getUser1(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "return");
        }

        return "redirect:/tic-tac-toe/pvp";
    }

}
