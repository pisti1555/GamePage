package project.gamepage.data.model.game.fly_in_the_web;

import java.util.Random;

public class FITW {
    public Integer id;
    public final Field_FITW[] field;
    public final Piece_FITW fly;
    public final Piece_FITW[] spider;


    public short gameMode;
    public final short PVP = 1;
    public final short PVS = 2;
    public final short PVF = 3;
    public final Random random;
    public boolean isGameRunning;
    public boolean isFlysTurn;
    public int pieceWon;
    public int flyStepsDone;
    public int spiderStepsDone;



    public FITW() {
        random = new Random();
        CreateBoard_FITW cb = new CreateBoard_FITW();
        field = cb.giveField();
        fly = cb.giveFly();
        spider = cb.giveSpiders();
        isGameRunning = true;
        isFlysTurn = true;
        pieceWon = 0;
        flyStepsDone = 0;
        spiderStepsDone = 0;
    }

    public Field_FITW[] getField() {
        return field;
    }

    public Piece_FITW getFly() {
        return fly;
    }

    public Piece_FITW[] getSpider() {
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

    public Field_FITW[] cloneBoard() {
        Field_FITW[] clonedBoard = new CreateBoard_FITW().giveField();

        for (int i = 0; i < clonedBoard.length; i++) {
            clonedBoard[i].setPiece(Pieces_FITW.EMPTY);
        }

        for (int i = 0; i < clonedBoard.length; i++) {
            if (field[i].getPiece().equals(Pieces_FITW.FLY)) {
                clonedBoard[i].setPiece(Pieces_FITW.FLY);
            }
            if (field[i].getPiece().equals(Pieces_FITW.SPIDER)) {
                clonedBoard[i].setPiece(Pieces_FITW.SPIDER);
            }
        }

        return clonedBoard;
    }
}
