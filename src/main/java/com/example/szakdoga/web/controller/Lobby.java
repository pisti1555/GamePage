package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.game.PvP;
import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.service.GameService;
import com.example.szakdoga.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    SimpMessagingTemplate template;
    UserFriendsService friendsService;
    @Autowired
    public Lobby(GameService service, SimpMessagingTemplate template, UserFriendsService friendsService) {
        this.service = service;
        this.template = template;
        this.friendsService = friendsService;
    }

    @GetMapping
    public String getLobby(Model model, Principal principal) {
        String username = principal.getName();
        PvP game = service.getGame(username);
        model.addAttribute("username", username);
        model.addAttribute("game", game);
        model.addAttribute("string", game.getGame());
        model.addAttribute("invites", service.getInvites(username));
        model.addAttribute("player1", game.getUser1());
        model.addAttribute("player2", game.getUser2());

        ResponseEntity<Map<String, Object>> responseEntity = friendsService.getUserFriendsList(new UserFriendsListRequestEntity(principal.getName()));
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseBody != null) {
            model.addAttribute("friends", responseBody.get("friends"));
        }
        return "lobby";
    }

    @MessageMapping("/lobby/invite")
    @SendTo("/topic/lobby")
    @PostMapping("/invite")
    public String inviteFriend(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        service.inviteFriend(principal.getName(), friendUsername);
        template.convertAndSend("/topic/lobby", service.getInvites(friendUsername));
        return "redirect:/lobby";
    }

    @MessageMapping("/lobby/join")
    @SendTo("/topic/lobby")
    @PostMapping("/join")
    public String joinLobby(@RequestParam("inviterName") String inviterName, Principal principal, Model model) {
        PvP game = service.joinLobby(inviterName, principal.getName());
        if (game != null) {
            model.addAttribute("string", game.getGame());
            template.convertAndSend("/topic/lobby", game);
            return "redirect:/lobby";
        } else {
            return "redirect:/lobby?error=joinFailed";
        }
    }

    @MessageMapping("/lobby/start")
    @SendTo("/topic/lobby")
    @PostMapping("/start")
    public String startGame(Model model, Principal principal) {
        String username = principal.getName();
        PvP game = service.getGame(username);

        if (game.isReady()) {
            template.convertAndSend("/topic/lobby/start", game);
            return "redirect:/game";
        } else {
            return "redirect:/lobby?error=gameNotReady";
        }
    }
}