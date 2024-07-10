package project.gamepage.web.controller.game.tic_tac_toe;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tic-tac-toe")
public class LobbyController_TicTacToe {
    GameService_TicTacToe service;
    UserFriendsService friendsService;
    InvitationService invitationService;
    SimpMessagingTemplate template;

    @Autowired
    public LobbyController_TicTacToe(GameService_TicTacToe service, UserFriendsService friendsService, InvitationService invitationService, SimpMessagingTemplate template) {
        this.service = service;
        this.friendsService = friendsService;
        this.invitationService = invitationService;
        this.template = template;
    }

    @GetMapping
    public String redirectURL(Principal principal, Model model) {
        return "redirect:/tic-tac-toe/lobby";
    }

    @GetMapping("/lobby")
    public String getLobby_TicTacToe(Principal principal, Model model) {
        String username = principal.getName();
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());

        if (pvp.getUser1().equals(username) && pvp.isOver()) {
            pvp.setUser1InGame(false);
        }
        if (pvp.getUser2() != null && pvp.isOver()) {
            if (pvp.getUser2().equals(username)) {
                pvp.setUser2InGame(false);
            }
        }

        model.addAttribute("username", username);
        model.addAttribute("lobby", pvp);
        model.addAttribute("isReady", pvp.isReadyToStart());

        return "game/tic_tac_toe/lobby";
    }

    @GetMapping("/ready")
    public String ready(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        if (pvp.getUser2() == null) return "redirect:/tic-tac-toe/lobby";
        if (pvp.getUser1().equals(principal.getName())) {
            pvp.setUser1Ready(!pvp.isUser1Ready());
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "update");
            return "redirect:/tic-tac-toe/lobby";
        }
        if (pvp.getUser2().equals(principal.getName())) {
            pvp.setUser2Ready(!pvp.isUser2Ready());
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "update");
            return "redirect:/tic-tac-toe/lobby";
        }
        return "redirect:/tic-tac-toe/lobby";
    }

    @GetMapping("/start")
    public String startGame_TicTacToe_PvP(Model model, Principal principal) {
        String username = principal.getName();
        PvP<TicTacToe> pvp = service.getPvP(username);
        if (pvp.isReadyToStart()) {
            pvp.setBoard(new TicTacToe());
            pvp.setUser1InGame(true);
            pvp.setUser2InGame(true);
            pvp.setUser1Ready(false);
            pvp.setUser2Ready(false);
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/start", "update");
            return "redirect:/tic-tac-toe/game/pvp";
        } else {
            return "redirect:/tic-tac-toe/lobby?error=gameNotReady";
        }
    }

    @PostMapping("/invite")
    public String inviteFriend_TicTacToe(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        invitationService.inviteFriend(principal.getName(), friendUsername, "TicTacToe");
        template.convertAndSendToUser(friendUsername, "/topic/invites", "update");
        return "redirect:/tic-tac-toe/lobby";
    }

    @PostMapping("/join")
    public String joinLobby_TicTacToe(@RequestParam("inviter") String inviter, Principal principal, Model model) {
        PvP<TicTacToe> pvp = service.joinLobby(inviter, principal.getName(), "TicTacToe");
        if (pvp != null) {
            template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
            return "redirect:/tic-tac-toe/lobby";
        } else {
            return "redirect:/tic-tac-toe/lobby?error=joinFailed";
        }
    }

    @PostMapping("/decline-lobby-invitation")
    public String declineLobbyInvitation_TicTacToe(HttpServletRequest http, Principal principal, @RequestParam("inviter") String inviter) {
        invitationService.removeInvitation(principal.getName(), inviter, "TicTacToe");
        return "redirect:" + http.getHeader("Referer");
    }

    @GetMapping("/leave")
    public String quit_TicTacToe(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        if (pvp.getUser1() == null || pvp.getUser2() == null) return "redirect:/tic-tac-toe";
        service.quitLobby(principal.getName());
        if (pvp.getUser2() != null && pvp.getUser1().equals(principal.getName())) {
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "quit");
        }
        if (pvp.getUser1() != null && pvp.getUser2().equals(principal.getName())) {
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "quit");
        }
        return "redirect:/tic-tac-toe";
    }
}
