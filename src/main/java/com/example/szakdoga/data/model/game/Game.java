package com.example.szakdoga.data.model.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Game {
    String id;
    String user1;
    String user2;
    @JsonIgnore
    Board board;

    public Game(String user1, String user2, Board board) {
        this.id = user1;
        this.user1 = user1;
        this.user2 = user2;
        this.board = board;
    }

    public Game() {
    }

    public boolean isReady() {
        return user1 != null && user2 != null && board != null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
