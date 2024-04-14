package com.example.szakdoga.service;

import com.example.szakdoga.data.model.game.string.ProbaString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConnectionService {

    UserService userService;
    UserFriendsService userFriendsService;
    Map<String, ProbaString> userMap = new ConcurrentHashMap<>();

    @Autowired
    public ConnectionService(UserService userService, UserFriendsService userFriendsService) {
        this.userService = userService;
        this.userFriendsService = userFriendsService;
    }



    public ProbaString getLobby(String username) {
        return userMap.computeIfAbsent(username, key -> {
            return new ProbaString();
        });
    }

    public void createString(String username) {
        if (!userMap.containsKey(username)) {
            userMap.put(username, new ProbaString());
        }
    }

    public String getStringFromUser(String username) {
        return userMap.get(username).getProba();
    }





























    /*

    UserService userService;
    UserFriendsService userFriendsService;
    Map<String, ProbaString> userMap = new ConcurrentHashMap<>();

    @Autowired
    public ConnectionService(UserService userService, UserFriendsService userFriendsService) {
        this.userService = userService;
        this.userFriendsService = userFriendsService;
    }

    public ProbaString getProbaStringForUser(String username) {
        return userMap.computeIfAbsent(username, key -> {
            return new ProbaString();
        });
    }

    public void createString(String username) {
        if (!userMap.containsKey(username)) {
            userMap.put(username, new ProbaString());
        }
    }

    public String getStringFromUser(String username) {
        return userMap.get(username).getProba();
    }


     */
}