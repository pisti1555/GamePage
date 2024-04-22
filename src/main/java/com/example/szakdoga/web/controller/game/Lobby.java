package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.service.GameService;
import com.example.szakdoga.service.InvitationService;
import com.example.szakdoga.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/lobby")
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

        ResponseEntity<Map<String, Object>> responseEntity = friendsService.getUserFriendsList(new UserFriendsListRequestEntity(principal.getName()));
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseBody != null) {
            model.addAttribute("friends", responseBody.get("friends"));
        }
        return "game/lobby";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        invitationService.inviteFriend(principal.getName(), friendUsername);
        template.convertAndSendToUser(friendUsername, "/topic/invites", "update");
        return "redirect:/lobby";
    }

    @PostMapping("/join")
    public String joinLobby(@RequestParam("inviterName") String inviterName, Principal principal, Model model) {
        PvP pvP = service.joinLobby(inviterName, principal.getName());
        if (pvP != null) {
            template.convertAndSendToUser(inviterName, "/topic/lobby/update", "update");
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/update", "update");
            return "redirect:/lobby";
        } else {
            return "redirect:/lobby?error=joinFailed";
        }
    }

    @PostMapping("/start")
    public String startGame(Model model, Principal principal) {
        String username = principal.getName();
        PvP pvP = service.getPvP(username);

        if (pvP.isReadyToStart()) {
            pvP.setBoard(new Board());
            template.convertAndSendToUser(principal.getName(), "/topic/lobby/start", "update");
            template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/start", "update");
            return "redirect:/game/pvp";
        } else {
            return "redirect:/lobby?error=gameNotReady";
        }
    }

    @PostMapping("/leave")
    public String quit(Principal principal) {
        PvP pvP = service.quitLobby(principal.getName());
        template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "quit");
        template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "quit");
        return "redirect:/lobby";
    }

}