package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.User;
import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.service.GameService;
import com.example.szakdoga.service.InvitationService;
import com.example.szakdoga.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/game")
public class GameController {
    GameService service;
    UserService userService;
    SimpMessagingTemplate template;
    InvitationService invitationService;

    @Autowired
    public GameController(GameService service, UserService userService, SimpMessagingTemplate template, InvitationService invitationService) {
        this.service = service;
        this.userService = userService;
        this.template = template;
        this.invitationService = invitationService;
    }

    @GetMapping
    public String getGamePage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        return "game/gameMenu";
    }

    @GetMapping("/pvp")
    public String getGamePage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        PvP pvP = service.getPvP(username);
        if (pvP.getUser2() != null) {
            if (pvP.isReadyToStart()) {
                if (pvP.getUser1().equals(principal.getName())) {
                    pvP.setUser1InGame(true);
                }
                if (pvP.getUser2().equals(principal.getName())) {
                    pvP.setUser2InGame(true);
                }
            }

            boolean gameOver = service.getIsGameRunning(pvP.getBoard());
            int whoWon = service.whoWon(pvP.getBoard());
            int flyStepsDone = service.getFlyStepsDone(pvP.getBoard());
            int spiderStepsDone = service.getSpiderStepsDone(pvP.getBoard());
            int totalStepsDone = flyStepsDone + spiderStepsDone;

            gameOver(username);

            model.addAttribute("user1InGame", pvP.isUser1InGame());
            model.addAttribute("user2InGame", pvP.isUser2InGame());
            model.addAttribute("gameOver", gameOver);
            model.addAttribute("whoWon", whoWon);
            model.addAttribute("flySteps", flyStepsDone);
            model.addAttribute("spiderSteps", spiderStepsDone);
            model.addAttribute("totalSteps", totalStepsDone);
            return "game/spiderweb_pvp";
        }

        return "redirect:/lobby?error=noGameFound";
    }

    @GetMapping("/pvs")
    public String pvs(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        service.newGamePvC("pvs", principal.getName());
        return "game/spiderweb_pvs";
    }

    @GetMapping("/pvf")
    public String pvf(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        service.newGamePvC("pvf", principal.getName());
        return "game/spiderweb_pvf";
    }

    @GetMapping("/return-to-lobby")
    public String returnToLobby(Principal principal) {
        PvP pvP = service.getPvP(principal.getName());
        if (principal.getName().equals(pvP.getUser1())) {
            pvP.setUser1InGame(false);
            template.convertAndSendToUser(pvP.getUser2(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "return");
        } else {
            pvP.setUser2InGame(false);
            template.convertAndSendToUser(pvP.getUser1(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "return");
        }

        return "redirect:/lobby";
    }

    public void gameOver(String name) {
        User user = userService.findByUsername(name);
        PvP pvP = service.getPvP(user.getUsername());
        if (!pvP.getBoard().isGameRunning()) {
            service.gameOver(pvP, user);
            userService.update(user);
        }
    }
}