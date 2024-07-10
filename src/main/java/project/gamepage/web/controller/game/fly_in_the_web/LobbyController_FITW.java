package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/fly-in-the-web/lobby")
public class LobbyController_FITW {
    GameService_FITW service;
    InvitationService invitationService;
    SimpMessagingTemplate template;
    UserFriendsService friendsService;
    @Autowired
    public LobbyController_FITW(GameService_FITW service, InvitationService invitationService, SimpMessagingTemplate template, UserFriendsService friendsService) {
        this.service = service;
        this.invitationService = invitationService;
        this.template = template;
        this.friendsService = friendsService;
    }

    @GetMapping
    public String getLobby_FITW(Model model, Principal principal) {
        String username = principal.getName();
        PvP<FITW> pvp = service.getPvP(username);

        if (pvp.getUser1().equals(username) && pvp.isOver()) {
            pvp.setUser1InGame(false);
        }
        if (pvp.getUser2() != null && pvp.isOver()) {
            if (pvp.getUser2().equals(username)) {
                pvp.setUser2InGame(false);
            }
        }

        model.addAttribute("username", username);
        model.addAttribute("lobby", service.getPvP(username));
        model.addAttribute("isReady", pvp.isReadyToStart());

        return "game/fly_in_the_web/lobby";
    }

    @GetMapping("/swap-piece")
    public String swapPiece(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        if (!pvp.getUser1().equals(principal.getName())) return "redirect:/fly-in-the-web/lobby?swap-denied";
        short temp = pvp.getPrimaryPiece();
        pvp.setPrimaryPiece(pvp.getSecondaryPiece());
        pvp.setSecondaryPiece(temp);
        if (pvp.getUser2() != null) {
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "update");
        }
        return "redirect:/fly-in-the-web/lobby?swap-success";
    }

    @GetMapping("/ready")
    public String ready(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        if (pvp.getUser2() == null) return "redirect:/fly-in-the-web/lobby";
        if (pvp.getUser1().equals(principal.getName())) {
            pvp.setUser1Ready(!pvp.isUser1Ready());
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "update");
            return "redirect:/fly-in-the-web/lobby";
        }
        if (pvp.getUser2().equals(principal.getName())) {
            pvp.setUser2Ready(!pvp.isUser2Ready());
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "update");
            return "redirect:/fly-in-the-web/lobby";
        }
        return "redirect:/fly-in-the-web/lobby";
    }

    @PostMapping("/invite")
    public String inviteFriend_FITW(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        invitationService.inviteFriend(principal.getName(), friendUsername, "FITW");
        template.convertAndSendToUser(friendUsername, "/topic/invites", "update");
        return "redirect:/fly-in-the-web/lobby";
    }

    @PostMapping("/join")
    public String joinLobby_FITW(@RequestParam("inviter") String inviter, Principal principal, Model model) {
        PvP<FITW> pvp = service.joinLobby(inviter, principal.getName(), "FITW");
        if (pvp != null) {
            template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
            return "redirect:/fly-in-the-web/lobby";
        } else {
            return "redirect:/fly-in-the-web/lobby?error=joinFailed";
        }
    }

    @PostMapping("/decline-lobby-invitation")
    public String declineLobbyInvitation_FITW(HttpServletRequest http, Principal principal, @RequestParam("inviter") String inviter) {
        invitationService.removeInvitation(principal.getName(), inviter, "FITW");
        return "redirect:" + http.getHeader("Referer");
    }

    @GetMapping("/start")
    public String startGame_FITW(Model model, Principal principal) {
        String username = principal.getName();
        PvP<FITW> pvp = service.getPvP(username);
        if (pvp.isReadyToStart()) {
            pvp.setBoard(new FITW());
            if (pvp.getPrimaryPiece() == 2) pvp.getBoard().isFlysTurn = false;
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/start", "update");
            return "redirect:/fly-in-the-web/game/pvp";
        } else return "redirect:/fly-in-the-web/lobby?error=gameNotReady";
    }

    @GetMapping("/leave")
    public String quit_FITW(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        if (pvp.getUser1() == null || pvp.getUser2() == null) return "redirect:/fly-in-the-web/lobby";
        service.quitLobby(principal.getName());
        if (pvp.getUser2() != null && pvp.getUser1().equals(principal.getName())) {
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/update", "quit");
        }
        if (pvp.getUser1() != null && pvp.getUser2().equals(principal.getName())) {
            template.convertAndSendToUser(pvp.getUser1(), "/topic/lobby/update", "quit");
        }
        return "redirect:/fly-in-the-web/lobby";
    }

}