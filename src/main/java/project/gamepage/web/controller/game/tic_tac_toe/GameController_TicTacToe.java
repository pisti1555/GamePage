package project.gamepage.web.controller.game.tic_tac_toe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
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

    @GetMapping("/ai-x")
    public String getGamePageAI_X(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("x", principal.getName());
        return "game/tic_tac_toe/game_page_ai";
    }

    @GetMapping("/ai-o")
    public String getGamePageAI_O(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("o", principal.getName());
        return "game/tic_tac_toe/game_page_ai";
    }

    @GetMapping("/pvp")
    public String getGamePagePvP(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("username", username);

        PvP<TicTacToe> pvp = service.getPvP(username);
        if (pvp.isUser1InGame() && pvp.isUser2InGame()) return "game/tic_tac_toe/game_page_pvp";
        if (pvp.getUser2() == null || !pvp.isReadyToStart()) return "redirect:/tic-tac-toe/lobby";
        pvp.setUser1InGame(true);
        pvp.setUser2InGame(true);
        pvp.setUser1Ready(false);
        pvp.setUser2Ready(false);
        return "game/tic_tac_toe/game_page_pvp";
    }

    @GetMapping("/leave-game")
    public String leaveGame(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        if (!pvp.isInProgress()) return "redirect:/tic-tac-toe/lobby";
        if (pvp.getUser1().equals(principal.getName())) {
            service.quitLobby(principal.getName());
            template.convertAndSendToUser(pvp.getUser2(), "/topic/game/update", "return");
        }
        if (pvp.getUser2().equals(principal.getName())) {
            service.quitLobby(principal.getName());
            template.convertAndSendToUser(pvp.getUser1(), "/topic/game/update", "return");
        }
        return "redirect:/tic-tac-toe/lobby";
    }

    @GetMapping("/return-to-lobby")
    public String returnToLobby_TicTacToe(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        if (principal.getName().equals(pvp.getUser1())) {
            template.convertAndSendToUser(pvp.getUser2(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "return");
        } else {
            template.convertAndSendToUser(pvp.getUser1(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "return");
        }

        return "redirect:/tic-tac-toe/lobby";
    }
}
