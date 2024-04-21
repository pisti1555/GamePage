package com.example.szakdoga.data.model.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PvP {
    String id;
    String user1;
    String user2;
    boolean user1InGame;
    boolean user2InGame;
    @JsonIgnore
    Board board;

    public PvP(String user1, String user2, Board board) {
        this.id = user1;
        this.user1 = user1;
        this.user2 = user2;
        this.user1InGame = false;
        this.user2InGame = false;
        this.board = board;
    }

    public PvP() {
    }

    public boolean isReadyToStart() {
        return user1 != null && user2 != null && board != null && !user1InGame && !user2InGame;
    }

    public boolean isInProgress() {
        return user1 != null && user2 != null && board != null && user1InGame && user2InGame;
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

    public boolean isUser1InGame() {
        return user1InGame;
    }

    public void setUser1InGame(boolean user1InGame) {
        this.user1InGame = user1InGame;
    }

    public boolean isUser2InGame() {
        return user2InGame;
    }

    public void setUser2InGame(boolean user2InGame) {
        this.user2InGame = user2InGame;
    }
}
