package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.game.Game;
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
        Game game = service.getGame(username);
        model.addAttribute("username", username);
        //model.addAttribute("game", game);
        model.addAttribute("invites", service.getInvites(username));
        model.addAttribute("invCount", service.invCount(username));
        model.addAttribute("player1", game.getUser1());
        model.addAttribute("player2", game.getUser2());

        ResponseEntity<Map<String, Object>> responseEntity = friendsService.getUserFriendsList(new UserFriendsListRequestEntity(principal.getName()));
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseBody != null) {
            model.addAttribute("friends", responseBody.get("friends"));
        }
        return "game/lobby";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam("friendUsername") String friendUsername, Principal principal) {
        service.inviteFriend(principal.getName(), friendUsername);
        template.convertAndSendToUser(friendUsername, "/topic/lobby/update", "update");
        return "redirect:/lobby";
    }

    @PostMapping("/join")
    public String joinLobby(@RequestParam("inviterName") String inviterName, Principal principal, Model model) {
        Game game = service.joinLobby(inviterName, principal.getName());
        if (game != null) {
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
        Game game = service.getGame(username);
        template.convertAndSendToUser(principal.getName(), "/topic/lobby/start", "update");
        template.convertAndSendToUser(game.getUser2(), "/topic/lobby/start", "update");

        if (game.isReady()) {
            return "redirect:/game/pvp";
        } else {
            return "redirect:/lobby?error=gameNotReady";
        }
    }

}