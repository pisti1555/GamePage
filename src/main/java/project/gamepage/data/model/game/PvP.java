package project.gamepage.data.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PvP <Game> {
    String id;
    String user1;
    String user2;
    short primaryPiece; //FLY (FITW) or X (Tic-Tac-Toe) - means which player uses this piece
    short secondaryPiece; //SPIDERS (FITW) or O (Tic-Tac-Toe) - means which player uses this piece
    boolean user1InGame;
    boolean user2InGame;
    boolean isUser1Ready;
    boolean isUser2Ready;
    boolean isOver;
    @JsonIgnore
    Game board;

    public PvP(String user1, String user2, Game board) {
        this.id = user1;
        this.user1 = user1;
        this.user2 = user2;
        this.primaryPiece = 1;
        this.secondaryPiece = 2;
        this.user1InGame = false;
        this.user2InGame = false;
        this.isUser1Ready = false;
        this.isUser2Ready = false;
        this.isOver = false;
        this.board = board;
    }

    public PvP() {
    }

    public boolean isReadyToStart() {
        return user1 != null && user2 != null && board != null && isUser1Ready && isUser2Ready && !user1InGame && !user2InGame;
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

    public short getPrimaryPiece() {
        return primaryPiece;
    }

    public void setPrimaryPiece(short primaryPiece) {
        this.primaryPiece = primaryPiece;
    }

    public short getSecondaryPiece() {
        return secondaryPiece;
    }

    public void setSecondaryPiece(short secondaryPiece) {
        this.secondaryPiece = secondaryPiece;
    }

    public Game getBoard() {
        return board;
    }

    public void setBoard(Game board) {
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

    public boolean isUser1Ready() {
        return isUser1Ready;
    }

    public void setUser1Ready(boolean user1Ready) {
        isUser1Ready = user1Ready;
    }

    public boolean isUser2Ready() {
        return isUser2Ready;
    }

    public void setUser2Ready(boolean isUser2Ready) {
        this.isUser2Ready = isUser2Ready;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean isOver) {
        this.isOver = isOver;
    }
}
