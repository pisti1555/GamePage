package com.example.szakdoga.data.model.game.spiderweb;

import java.util.Random;

public class Board {
    private Integer id;
    private final Field[] field;
    private final Piece fly;
    private final Piece[] spider;


    private short gameMode;
    private final short PVP = 1;
    private final short PVS = 2;
    private final short PVF = 3;
    private final Random random;
    private boolean isGameRunning = false;
    private boolean isFlysTurn = true;
    private int pieceWon = 0;
    private int flyStepsDone = 0;
    private int spiderStepsDone = 0;



    public Board() {
        random = new Random();
        CreateBoard cb = new CreateBoard();
        field = cb.giveField();
        fly = cb.giveFly();
        spider = cb.giveSpiders();
    }

    public Field[] getField() {
        return field;
    }

    public Piece getFly() {
        return fly;
    }

    public Piece[] getSpider() {
        return spider;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getGameMode() {
        return gameMode;
    }

    public void setGameMode(short gameMode) {
        this.gameMode = gameMode;
    }

    public short getPVP() {
        return PVP;
    }

    public short getPVS() {
        return PVS;
    }

    public short getPVF() {
        return PVF;
    }

    public Random getRandom() {
        return random;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public boolean isFlysTurn() {
        return isFlysTurn;
    }

    public void setFlysTurn(boolean flysTurn) {
        isFlysTurn = flysTurn;
    }

    public int getPieceWon() {
        return pieceWon;
    }

    public void setPieceWon(int pieceWon) {
        this.pieceWon = pieceWon;
    }

    public int getFlyStepsDone() {
        return flyStepsDone;
    }

    public void setFlyStepsDone(int flyStepsDone) {
        this.flyStepsDone = flyStepsDone;
    }

    public int getSpiderStepsDone() {
        return spiderStepsDone;
    }

    public void setSpiderStepsDone(int spiderStepsDone) {
        this.spiderStepsDone = spiderStepsDone;
    }
}
