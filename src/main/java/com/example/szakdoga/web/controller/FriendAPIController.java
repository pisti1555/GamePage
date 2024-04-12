package com.example.szakdoga.web.controller;

import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import com.example.szakdoga.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FriendAPIController {
    private UserFriendsService userFriendsService;

    @Autowired
    public FriendAPIController(UserFriendsService userFriendsService) {
        this.userFriendsService = userFriendsService;
    }

    @RequestMapping(value = "/userFriendRequest", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> userFriendRequest(@RequestBody UserFriendsRequestEntity userFriendsRequestEntity) {
        return this.userFriendsService.addUserFriends(userFriendsRequestEntity);
    }

    @RequestMapping(value = "/getUserFriendList", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getUserFriendList(@RequestBody UserFriendsListRequestEntity userFriendsListRequestEntity) {
        return this.userFriendsService.getUserFriendsList(userFriendsListRequestEntity);
    }

    @RequestMapping(value = "/getCommonUserFriends", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getCommonUserFriends(@RequestBody UserFriendsRequestEntity userFriendsRequestEntity) {
        return this.userFriendsService.getCommonUserFriends(userFriendsRequestEntity);
    }
}
