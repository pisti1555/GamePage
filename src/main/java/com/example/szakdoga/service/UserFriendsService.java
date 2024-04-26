package com.example.szakdoga.service;

import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserFriendsService {
    ResponseEntity<Map<String, Object>> addUserFriends(UserFriendsRequestEntity userFriendsRequestEntity);

    ResponseEntity<Map<String, Object>> getUserFriendsList(UserFriendsListRequestEntity userFriendsListRequestEntity);

    ResponseEntity<Map<String, Object>> getCommonUserFriends(UserFriendsRequestEntity userFriendsRequestEntity);

    List<String> getFriendRequests(String principal);
    int getFriendRequestCount(String principal);
    void declineFriendRequest(String inviter, String invited);
    void deleteFriends(String user1Id, String user2Id);
    boolean isFriend(String principal, String user);
}
