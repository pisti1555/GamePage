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
import java.util.List;

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
        model.addAttribute("username", username);
        model.addAttribute("user1InGame", pvp.isUser1InGame());
        model.addAttribute("user2InGame", pvp.isUser2InGame());
        model.addAttribute("isReady", pvp.isReadyToStart());
        model.addAttribute("player1", pvp.getUser1());
        model.addAttribute("player2", pvp.getUser2());

        List<String> friendList;
        friendList = friendsService.getUserFriendsList(principal.getName());
        model.addAttribute("friends", friendList);

        return "game/fly_in_the_web/lobby";
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
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/update", "update");
            return "redirect:/fly-in-the-web/lobby";
        } else {
            return "redirect:/fly-in-the-web/lobby?error=joinFailed";
        }
    }

    @PostMapping("/decline-lobby-invitation")
    public String declineLobbyInvitation_FITW(HttpServletRequest http, Principal principal, @RequestParam("inviter") String inviter) {
        invitationService.declineInvite(principal.getName(), inviter, "FITW");
        return "redirect:" + http.getHeader("Referer");
    }

    @PostMapping("/start")
    public String startGame_FITW(Model model, Principal principal) {
        String username = principal.getName();
        PvP<FITW> pvp = service.getPvP(username);

        if (pvp.isReadyToStart()) {
            pvp.setBoard(new FITW());
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/start", "update");
            template.convertAndSendToUser(pvp.getUser2(), "/topic/lobby/start", "update");
            return "redirect:/fly-in-the-web/game/pvp";
        } else {
            return "redirect:/fly-in-the-web/lobby?error=gameNotReady";
        }
    }

    @PostMapping("/leave")
    public String quit_FITW(Principal principal) {
        PvP<FITW> pvP = service.quitLobby(principal.getName());
        template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "quit");
        template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "quit");
        return "redirect:/fly-in-the-web/lobby";
    }

}