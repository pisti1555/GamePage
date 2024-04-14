package com.example.szakdoga.request;

public class UserFriendsListRequestEntity {
    public UserFriendsListRequestEntity() {
    }
    public UserFriendsListRequestEntity(String username) {
        this.username = username;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
