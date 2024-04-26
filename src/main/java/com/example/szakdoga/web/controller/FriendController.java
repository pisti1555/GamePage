package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.user.User;
import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import com.example.szakdoga.service.InvitationService;
import com.example.szakdoga.service.UserFriendsService;
import com.example.szakdoga.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/friends")
public class FriendController {
    UserFriendsService service;
    UserService userService;
    InvitationService invitationService;
    SimpMessagingTemplate template;
    @Autowired
    public FriendController(UserFriendsService service, UserService userService, InvitationService invitationService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.invitationService = invitationService;
        this.template = template;
    }


    @GetMapping
    public String getFriendList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        ResponseEntity<Map<String, Object>> responseEntity = service.getUserFriendsList(new UserFriendsListRequestEntity(principal.getName()));
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseBody != null) {
            model.addAttribute("success", responseBody.get("success"));
            model.addAttribute("friends", responseBody.get("friends"));
            model.addAttribute("count", responseBody.get("count"));
        }

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        model.addAttribute("friendRequests", service.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", service.getFriendRequestCount(principal.getName()));

        return "player/friendList";
    }

    @GetMapping("/add")
    public String getAddFriend(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        model.addAttribute("friendRequests", service.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", service.getFriendRequestCount(principal.getName()));

        return "player/addFriend";
    }

    @PostMapping("/add")
    public String addFriend(Model model, Principal principal, UserFriendsRequestEntity request, HttpServletRequest http) {
        List<String> friends = new ArrayList<>();
        String inviter = principal.getName();
        String invited = request.getFriends().get(1);

        User invitedUser = userService.findByUsername(invited);
        if (invitedUser == null) {
            return "redirect:/friends/add?userNotFound";
        }

        friends.add(inviter);
        friends.add(invited);
        request.setFriends(friends);

        service.addUserFriends(request);
        template.convertAndSendToUser(invited, "/topic/invites", "friend-request");
        return "redirect:" + http.getHeader("Referer");
    }


    @PostMapping("/decline-friend-request")
    public String declineFriendRequest(Model model, Principal principal, UserFriendsRequestEntity request, HttpServletRequest http) {
        List<String> friends = new ArrayList<>();
        String inviter = principal.getName();
        String invited = request.getFriends().get(1);

        friends.add(inviter);
        friends.add(invited);
        request.setFriends(friends);

        service.declineFriendRequest(inviter, invited);
        return "redirect:" + http.getHeader("Referer");
    }

    @PostMapping("/delete-friend")
    public String deleteFriend(Model model, Principal principal, UserFriendsRequestEntity request, HttpServletRequest http) {
        List<String> friends = new ArrayList<>();
        String user1 = principal.getName();
        String user2 = request.getFriends().get(1);

        friends.add(user1);
        friends.add(user2);
        request.setFriends(friends);

        service.deleteFriends(user1, user2);
        return "redirect:" + http.getHeader("Referer");
    }
}