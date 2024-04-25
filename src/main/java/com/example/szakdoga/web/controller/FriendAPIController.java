package com.example.szakdoga.web.controller;

import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import com.example.szakdoga.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class FriendAPIController {
    private UserFriendsService userFriendsService;

    @Autowired
    public FriendAPIController(UserFriendsService userFriendsService) {
        this.userFriendsService = userFriendsService;
    }

    @PostMapping("/userFriendRequest")
    public ResponseEntity<Map<String, Object>> userFriendRequest(@RequestBody UserFriendsRequestEntity userFriendsRequestEntity) {
        return this.userFriendsService.addUserFriends(userFriendsRequestEntity);
    }

    @PostMapping("/getUserFriendList")
    public ResponseEntity<Map<String, Object>> getUserFriendList(@RequestBody UserFriendsListRequestEntity userFriendsListRequestEntity) {
        return this.userFriendsService.getUserFriendsList(userFriendsListRequestEntity);
    }
}
