package com.example.szakdoga.service;

import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserFriendsService {
    ResponseEntity<Map<String, Object>> addUserFriends(UserFriendsRequestEntity userFriendsRequestEntity);

    ResponseEntity<Map<String, Object>> getUserFriendsList(UserFriendsListRequestEntity userFriendsListRequestEntity);

    ResponseEntity<Map<String, Object>> getCommonUserFriends(UserFriendsRequestEntity userFriendsRequestEntity);
}
