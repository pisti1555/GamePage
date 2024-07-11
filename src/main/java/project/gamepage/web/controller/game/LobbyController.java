package project.gamepage.web.controller.game;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;
import project.gamepage.service.invitations.InvitationService;

import java.security.Principal;

@Controller
@RequestMapping("/lobby")
public class LobbyController {
    GameService_FITW service_fitw;
    GameService_TicTacToe service_ticTacToe;
    InvitationService invitationService;
    SimpMessagingTemplate template;
    UserFriendsService friendsService;

    @Autowired
    public LobbyController(GameService_FITW service_fitw, GameService_TicTacToe service_ticTacToe, InvitationService invitationService, SimpMessagingTemplate template, UserFriendsService friendsService) {
        this.service_fitw = service_fitw;
        this.service_ticTacToe = service_ticTacToe;
        this.invitationService = invitationService;
        this.template = template;
        this.friendsService = friendsService;
    }

    @GetMapping("/fitw")
    public String getLobby_FITW(Model model, Principal principal) {
        String username = principal.getName();
        PvP<FITW> pvp = service_fitw.getPvP(username);
        if (pvp.getUser1().equals(username) && pvp.isOver()) {
            pvp.setUser1InGame(false);
        }
        if (pvp.getUser2() != null && pvp.isOver()) {
            if (pvp.getUser2().equals(username)) {
                pvp.setUser2InGame(false);
            }
        }
        model.addAttribute("username", username);
        model.addAttribute("lobby", service_fitw.getPvP(username));
        model.addAttribute("isReady", pvp.isReadyToStart());
        return "game/fly_in_the_web/lobby";
    }

    @GetMapping("/tic-tac-toe")
    public String getLobby_TicTacToe(Principal principal, Model model) {
        String username = principal.getName();
        PvP<TicTacToe> pvp = service_ticTacToe.getPvP(principal.getName());
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

    @PostMapping("/invite")
    public String inviteFriend_FITW(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        invitationService.inviteFriend(principal.getName(), friendUsername, "FITW");
        template.convertAndSendToUser(friendUsername, "/topic/invites", "update");
        return "redirect:/fly-in-the-web/lobby";
    }

    @PostMapping("/decline-lobby-invitation")
    public String declineLobbyInvitation_FITW(HttpServletRequest http, Principal principal, @RequestParam("inviter") String inviter) {
        invitationService.removeInvitation(principal.getName(), inviter, "FITW");
        return "redirect:" + http.getHeader("Referer");
    }

    @PostMapping("/join")
    public String joinLobby_FITW(@RequestParam("inviter") String inviter, @RequestParam("game")String game, Principal principal, Model model) {
        if (game.equals("FITW")) {
            PvP<FITW> pvp = service_fitw.joinLobby(inviter, principal.getName(), "FITW");
            if (pvp != null) {
                template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
                return "redirect:/fly-in-the-web/lobby";
            } else {
                return "redirect:/fly-in-the-web/lobby?error=joinFailed";
            }
        }
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = service_ticTacToe.joinLobby(inviter, principal.getName(), "TicTacToe");
            if (pvp != null) {
                template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
                return "redirect:/tic-tac-toe/lobby";
            } else {
                return "redirect:/tic-tac-toe/lobby?error=joinFailed";
            }
        }
        return null;
    }


}
