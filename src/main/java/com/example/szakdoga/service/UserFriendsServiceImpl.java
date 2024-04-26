package com.example.szakdoga.service;

import com.example.szakdoga.data.model.user.User;
import com.example.szakdoga.data.repository.FriendRequestDao;
import com.example.szakdoga.data.repository.UserFriendDao;
import com.example.szakdoga.data.repository.UserRepository;
import com.example.szakdoga.request.UserFriendsListRequestEntity;
import com.example.szakdoga.request.UserFriendsRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFriendsServiceImpl implements UserFriendsService {

    private final FriendRequestDao request;
    private final UserFriendDao dao;
    private final UserRepository userRepository;

    @Autowired
    public UserFriendsServiceImpl(UserFriendDao dao, FriendRequestDao request, UserRepository userRepository) {
        this.request = request;
        this.dao = dao;
        this.userRepository = userRepository;
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

        if (user1.getUserFriendRequest().contains(user2) && user2.getUserFriendRequest().contains(user1)) {
            user1.addFriend(user2);
            user2.addFriend(user1);
            this.dao.save(user1);
            this.dao.save(user2);

            this.request.deleteFriendRequest(user1.getId(), user2.getId());
            this.request.deleteFriendRequest(user2.getId(), user1.getId());

            result.put("Success", true);
        }

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
    public List<String> getFriendRequests(String username) {
        List<String> invites = new ArrayList<>();

        User client = userRepository.findByUsername(username);
        List<User> requests = userRepository.findAll();

        for (User user : requests) {
            if (user.getUserFriendRequest().contains(client)) {
                if (!isFriend(username, user.getUsername())) {
                    invites.add(user.getUsername());
                }
            }
        }

        return invites;
    }

    @Override
    public int getFriendRequestCount(String principal) {
        return getFriendRequests(principal).size();
    }

    @Override
    public void declineFriendRequest(String inviter, String invited) {
        User inviterUser = request.findByUsername(inviter);
        User invitedUser = request.findByUsername(invited);
        if (inviterUser != null && invitedUser != null) {
            Long inviterId = inviterUser.getId();
            Long invitedId = invitedUser.getId();

            request.deleteFriendRequest(inviterId, invitedId);
            request.deleteFriendRequest(invitedId, inviterId);
        }
    }

    @Override
    public void deleteFriends(String username1, String username2) {
        User user1 = request.findByUsername(username1);
        User user2 = request.findByUsername(username2);
        if (user1 != null && user2 != null) {
            Long user1Id = user1.getId();
            Long user2Id = user2.getId();

            dao.deleteFriend(user1Id, user2Id);
            dao.deleteFriend(user2Id,  user1Id);
        }
    }


    @Override
    public boolean isFriend(String principal, String username) {
        User client = this.dao.findByUsername(principal);
        User user = this.dao.findByUsername(username);

        return user != null && user.getUserFriend().contains(client) || client == user;
    }
}
