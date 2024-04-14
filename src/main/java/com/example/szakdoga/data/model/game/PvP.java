package com.example.szakdoga.data.model.game;

public class PvP {
    String user1;
    String user2;
    String string;

    public PvP(String user1, String user2, String game) {
        this.user1 = user1;
        this.user2 = user2;
        this.string = game;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getGame() {
        return string;
    }

    public void setGame(String game) {
        this.string = game;
    }
}
