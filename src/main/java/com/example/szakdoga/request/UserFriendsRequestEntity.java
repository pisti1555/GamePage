package com.example.szakdoga.request;

import java.util.List;

public class UserFriendsRequestEntity {
    private List<String> friends;

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
