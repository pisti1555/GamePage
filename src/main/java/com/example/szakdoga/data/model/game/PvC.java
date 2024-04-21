package com.example.szakdoga.data.model.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;

public class PvC {
    String user;
    Board board;

    public PvC(String user, Board board) {
        this.user = user;
        this.board = board;
    }

    public String getUser() {
        return user;
    }

    public void setUsername(String user) {
        this.user = user;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
