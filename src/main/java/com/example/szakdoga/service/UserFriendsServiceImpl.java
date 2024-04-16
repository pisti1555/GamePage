package com.example.szakdoga.service;

import com.example.szakdoga.data.model.User;
import com.example.szakdoga.data.repository.FriendRequestDao;
import com.example.szakdoga.data.repository.UserFriendDao;
import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFriendsServiceImpl implements UserFriendsService {

    private final FriendRequestDao request;
    private final UserFriendDao dao;

    @Autowired
    public UserFriendsServiceImpl(UserFriendDao dao, FriendRequestDao request) {
        this.request = request;
        this.dao = dao;
    }

    private User saveIfNotExist(String username) {
        User existingUser = this.request.findByUsername(username);
        if (existingUser == null) {
            existingUser = new User();
            existingUser.setUsername(username);
            return this.request.save(existingUser);
        } else {
            return existingUser;
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> addUserFriends(UserFriendsRequestEntity userFriendsRequestEntity) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (userFriendsRequestEntity == null) {
            result.put("Error : ", "Invalid request");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        if (CollectionUtils.isEmpty(userFriendsRequestEntity.getFriends())) {
            result.put("Error : ", "Friend list cannot be empty");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }
        if (userFriendsRequestEntity.getFriends().size() != 2) {
            result.put("Info : ", "Please provide 2 emails to make them friends");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        String un1 = userFriendsRequestEntity.getFriends().get(0);
        String un2 = userFriendsRequestEntity.getFriends().get(1);

        if (un1.equals(un2)) {
            result.put("Info : ", "Cannot make friends, if users are same");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        User user1 = null;
        User user2 = null;
        user1 = this.saveIfNotExist(un1);
        user2 = this.saveIfNotExist(un2);

        if (user1.getUserFriendRequest().contains(user2)) {
            result.put("Info : ", "Can't add, they are already friends");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
        }

        user1.sendFriendRequest(user2);
        this.request.save(user1);
        result.put("Success", true);

        if (user1.getUserFriendRequest().contains(user2 ) && user2.getUserFriendRequest().contains(user1)) {
            System.out.println("Barátok");
            user1.addFriend(user2);
            user2.addFriend(user1);
            this.dao.save(user1);
            this.dao.save(user2);

            result.put("Success", true);
        }
        System.out.println("Nem barátok");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserFriendsList(UserFriendsListRequestEntity userFriendsListRequestEntity) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (userFriendsListRequestEntity == null) {
            result.put("Error : ", "Invalid request");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        User user = this.dao.findByUsername(userFriendsListRequestEntity.getUsername());
        List<String> friendList = user.getUserFriend().stream().map(User::getUsername).collect(Collectors.toList());

        result.put("success", true);
        result.put("friends", friendList);
        result.put("count", friendList.size());

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCommonUserFriends(UserFriendsRequestEntity userFriendsRequestEntity) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (userFriendsRequestEntity == null) {
            result.put("Error : ", "Invalid request");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        User user1 = null;
        User user2 = null;
        user1 = this.dao.findByUsername(userFriendsRequestEntity.getFriends().get(0));
        user2 = this.dao.findByUsername(userFriendsRequestEntity.getFriends().get(1));

        if (user1.getUsername().equals(user2.getUsername())) {
            result.put("Info : ", "Both users are same");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
        }

        Set<User> friends = null;
        friends = user1.getUserFriend();
        friends.retainAll(user2.getUserFriend());

        result.put("success", true);
        result.put("friends", friends.stream().map(User::getUsername).collect(Collectors.toList()));
        result.put("count", friends.size());

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @Override
    public boolean isFriend(String principal, String username) {
        User client = this.dao.findByUsername(principal);
        User user = this.dao.findByUsername(username);

        return user != null && user.getUserFriend().contains(client) || client == user;
    }
}
