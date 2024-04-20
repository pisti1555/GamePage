package com.example.szakdoga.data.model.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Game {
    String id;
    String user1;
    String user2;
    String string;
    @JsonIgnore
    Board board;

    public Game(String user1, String user2, String game, Board board) {
        this.id = user1;
        this.user1 = user1;
        this.user2 = user2;
        this.string = game;
        this.board = board;
    }

    public Game() {
    }

    public boolean isReady() {
        return user1 != null && user2 != null && string != null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
