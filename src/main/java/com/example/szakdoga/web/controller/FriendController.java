package com.example.szakdoga.web.controller;

import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import com.example.szakdoga.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    public FriendController(UserFriendsService service) {
        this.service = service;
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
        return "friendList";
    }

    @GetMapping("/add")
    public String getAddFriend(Model model, Principal principal) {
        return "addFriend";
    }

    @PostMapping("/add")
    public String addFriend(Model model, Principal principal, UserFriendsRequestEntity request) {
        List<String> friends = new ArrayList<>();
        String inviter = principal.getName();
        String invited = request.getFriends().get(1);

        friends.add(inviter);
        friends.add(invited);
        request.setFriends(friends);

        service.addUserFriends(request);
        return "redirect:/friends/add";
    }


}
