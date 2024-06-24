package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.spiderweb.Board;
import project.gamepage.service.GameService;
import project.gamepage.service.InvitationService;
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
public class Lobby {
    GameService service;
    InvitationService invitationService;
    SimpMessagingTemplate template;
    UserFriendsService friendsService;
    @Autowired
    public Lobby(GameService service, InvitationService invitationService, SimpMessagingTemplate template, UserFriendsService friendsService) {
        this.service = service;
        this.invitationService = invitationService;
        this.template = template;
        this.friendsService = friendsService;
    }

    @GetMapping
    public String getLobby(Model model, Principal principal) {
        String username = principal.getName();
        PvP pvP = service.getPvP(username);
        model.addAttribute("username", username);
        model.addAttribute("invites", invitationService.getInvites(username));
        model.addAttribute("invCount", invitationService.invCount(username));
        model.addAttribute("friendRequests", friendsService.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", friendsService.getFriendRequestCount(principal.getName()));
        model.addAttribute("user1InGame", pvP.isUser1InGame());
        model.addAttribute("user2InGame", pvP.isUser2InGame());
        model.addAttribute("isReady", pvP.isReadyToStart());
        model.addAttribute("player1", pvP.getUser1());
        model.addAttribute("player2", pvP.getUser2());

        List<String> friendList;
        friendList = friendsService.getUserFriendsList(principal.getName());
        model.addAttribute("friends", friendList);

        return "game/fly_in_the_web/lobby";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        invitationService.inviteFriend(principal.getName(), friendUsername);
        template.convertAndSendToUser(friendUsername, "/topic/invites", "update");
        return "redirect:/fly-in-the-web/lobby";
    }

    @PostMapping("/join")
    public String joinLobby(@RequestParam("inviter") String inviter, Principal principal, Model model) {
        PvP pvP = service.joinLobby(inviter, principal.getName());
        if (pvP != null) {
            template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/update", "update");
            return "redirect:/fly-in-the-web/lobby";
        } else {
            return "redirect:/fly-in-the-web/lobby?error=joinFailed";
        }
    }

    @PostMapping("/decline-lobby-invitation")
    public String declineLobbyInvitation(HttpServletRequest http, Principal principal, @RequestParam("inviter") String inviter) {
        invitationService.declineInvite(principal.getName(), inviter);
        return "redirect:" + http.getHeader("Referer");
    }

    @PostMapping("/start")
    public String startGame(Model model, Principal principal) {
        String username = principal.getName();
        PvP pvP = service.getPvP(username);

        if (pvP.isReadyToStart()) {
            pvP.setBoard(new Board());
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/start", "update");
            template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/start", "update");
            return "redirect:/fly-in-the-web/game/pvp";
        } else {
            return "redirect:/fly-in-the-web/lobby?error=gameNotReady";
        }
    }

    @PostMapping("/leave")
    public String quit(Principal principal) {
        PvP pvP = service.quitLobby(principal.getName());
        template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "quit");
        template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "quit");
        return "redirect:/fly-in-the-web/lobby";
    }

}