package com.example.szakdoga.service;

import com.example.szakdoga.data.model.game.Game;
import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.data.model.game.spiderweb.Piece;
import com.example.szakdoga.data.model.game.spiderweb.Pieces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    List<Game> gameList;
    Map<String, String> invites;
    Random random;
    private final short PVP = 1;
    private final short PVS = 2;
    private final short PVF = 3;

    @Autowired
    public GameService() {
        this.gameList = new ArrayList<>();
        this.invites =  new HashMap<>();
        this.random = new Random();
    }

    public Game getGame(String username) {
        for (Game game : gameList) {
            if (game.getUser1().equals(username) || (game.getUser2() != null && game.getUser2().equals(username))) {
                return game;
            }
        }

        Game newGame = new Game(username, null, username, new Board());
        gameList.add(newGame);
        return newGame;
    }

    public Game joinLobby(String inviter, String invited) {
        for (Game game : gameList) {
            if (game.getUser1().equals(inviter)) {
                gameList.removeIf(i -> i.getUser1().equals(invited));
                game.setUser2(invited);

                for (Map.Entry<String, String> entry : invites.entrySet()) {
                    String invitedUser = entry.getKey();
                    String inviterUser = entry.getValue();
                    if (invited.equals(invitedUser)  && inviter.equals(inviterUser)) {
                        invites.remove(invitedUser);
                    }
                }

                return game;
            }
        }
        return null;
    }

    public void inviteFriend(String inviter, String invited) {
        invites.put(invited, inviter);
    }

    public List<String> getInvites(String username) {
        List<String> invitations = new ArrayList<>();

        for (Map.Entry<String, String> entry : invites.entrySet()) {
            String invited = entry.getKey();
            String inviter = entry.getValue();
            if (invited.equals(username)) {
                invitations.add(inviter);
            }
        }

        return invitations;
    }

    public int invCount(String username) {
        return getInvites(username).size();
    }

    public void changeString(String username, String string) {
        for (Game i : gameList) {
            if (i.getUser1().equals(username) || i.getUser2().equals(username)) {
                i.setGame(string);
                System.out.println("megváltozott");
            }
        }
    }



    //------------------------Game--------------------------

    public int move(Board board, int from, int to) {
        if (whichPiece(board, from) == board.getFly()) {
            moveFly(board, from, to);
            board.setFlyStepsDone(board.getFlyStepsDone()+1);
        }
        for (Piece p : board.getSpider()) {
            if (whichPiece(board, from) == p) {
                moveSpider(board, from, to, p);
                board.setSpiderStepsDone(board.getSpiderStepsDone()+1);
            }
        }

        return whoWon(board);
    }

    public boolean randomMoveFly(Board board) {
        int availableFields = board.getField()[board.getFly().location].getConnection().length;
        for (int i = 0; i < availableFields; i++) {
            if(board.getField()[board.getFly().location]
                    .getConnection()[i] == null) availableFields--;
        }

        if (board.isGameRunning()) {
            int randomConnection = random.nextInt(availableFields);
            int randomField = board.getField()[board.getFly().location]
                    .getConnection()[randomConnection].getNumber();

            if(isMoveValid(board, board.getFly().location, randomField)) {
                moveFly(board, board.getFly().location, randomField);
                return true;
            } else {
                randomMoveFly(board);
            }
        }
        return false;
    }

    public boolean randomMoveSpider(Board board) {
        Piece randomSpider = board.getSpider()[random.nextInt(board.getSpider().length)];

        int unavailableFields = 0;
        for (int i = 0; i < board.getField()[randomSpider.location].getConnection().length; i++) {
            if(board.getField()[randomSpider.location]
                    .getConnection()[i] == null) unavailableFields++;
        }
        int availableFields = 6 - unavailableFields;
        int randomConnection = random.nextInt(availableFields);
        int randomField = board.getField()[randomSpider.location]
                .getConnection()[randomConnection].getNumber();

        if (board.isGameRunning()) {
            if(isMoveValid(board, randomSpider.location, randomField)) {
                moveSpider(board, randomSpider.location, randomField, randomSpider);
                return true;
            } else {
                randomMoveSpider(board);
            }
        }
        return false;
    }

    public boolean isMoveValid(Board board, int from, int to) {
        boolean correctPiece = false;
        if(board.isFlysTurn()) {
            if (from == board.getFly().location) {
                correctPiece = true;
            } else return false;
        } else {
            for (int i = 0; i < board.getSpider().length; i++) {
                if (from == board.getSpider()[i].location) correctPiece = true;
            }
        }

        boolean areFieldsConnected = false;
        boolean isFromFieldEmpty = board.getField()[from].getPiece() == Pieces.EMPTY;
        boolean isToFieldEmpty = board.getField()[to].getPiece() == Pieces.EMPTY;

        for (int i = 0; i < board.getField()[from].getConnection().length; i++) {
            if (board.getField()[from].getConnection()[i] == board.getField()[to]) {
                areFieldsConnected = true;
            }
        }

        return isGameRunning(board) && !isFromFieldEmpty && isToFieldEmpty && correctPiece && areFieldsConnected;
    }

    public int[] getPositions(Board board) {
        int[] loc = new int[board.getSpider().length + 1];
        loc[0] = board.getFly().location;
        for (int i = 1; i < board.getSpider().length + 1; i++) {
            loc[i] = board.getSpider()[i-1].location;
        }

        return loc;
    }

    public short getGameMode(Board board) {
        return board.getGameMode();
    }

    public HashMap<Integer, ArrayList<Integer>> getConnections(Board board) {
        HashMap<Integer, ArrayList<Integer>> connections = new HashMap<>();
        for (int i = 0; i < board.getField().length; i++) {
            int numberOfConnections = 6;
            for (int j = 0; j < 6; j++) {
                if (board.getField()[i].getConnection()[j] == null) numberOfConnections--;
            }

            ArrayList<Integer> connectionOfField = new ArrayList<>();

            for (int j = 0; j < numberOfConnections; j++) {
                connectionOfField.add(board.getField()[i].getConnection()[j].getNumber());
                connections.put(i, connectionOfField);
            }
        }

        for (Map.Entry<Integer, ArrayList<Integer>> entry : connections.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Integer> values = entry.getValue();
        }

        return connections;
    }


    //---------------- Child methods of Main methods listed above ----------------
    public void moveFly(Board board, int from, int to) {
        if (isMoveValid(board, from, to)) {
            board.getField()[to].setPiece(board.getField()[from].getPiece());
            board.getField()[from].setPiece(Pieces.EMPTY);

            board.getFly().location = to;
            board.setFlysTurn(!board.isFlysTurn());
        } else {
            System.out.println("Invalid");
        }
        display(board);
    }

    public void moveSpider(Board board, int from, int to, Piece p) {
        if (isMoveValid(board, from, to)) {
            board.getField()[to].setPiece(board.getField()[from].getPiece());
            board.getField()[from].setPiece(Pieces.EMPTY);

            p.location = to;
            board.setFlysTurn(!board.isFlysTurn());
        } else {
            System.out.println("Invalid");
        }
        display(board);
    }

    public Piece whichPiece(Board board, int location) {
        Piece pieceToReturn = null;
        for (Piece p: board.getSpider()) {
            if (p.location == location) pieceToReturn = p;
        }
        if (board.getFly().location == location) pieceToReturn = board.getFly();

        return pieceToReturn;
    }

    public boolean isGameRunning(Board board) {
        if (
                board.getFly().location == 0 ||
                        board.getFly().location == 5 ||
                        board.getFly().location == 10 ||
                        board.getFly().location == 14 ||
                        board.getFly().location == 18 ||
                        board.getFly().location == 22
        ) {
            board.setGameRunning(false);
            board.setPieceWon(1);
        }

        int unavailableFields = 0;
        for (int i = 0; i < board.getField()[board.getFly().location].getConnection().length; i++) {
            if(
                    board.getField()[board.getFly().location].getConnection()[i] == null ||
                            board.getField()[board.getFly().location].getConnection()[i].getPiece() != Pieces.EMPTY
            ) unavailableFields++;
        }

        if (unavailableFields >= board.getField()[board.getFly().location].getConnection().length) {
            board.setGameRunning(false);
            board.setPieceWon(2);
        }
        return board.isGameRunning();
    }

    public boolean isFlysTurn(Board board) {
        return board.isFlysTurn();
    }

    public void display(Board board) {
        int flyLoc = -1;
        int spiderArrayIndex = 0;
        int[] spiderLoc = new int[board.getSpider().length];

        for (int i = 0; i < board.getField().length; i++) {
            if(board.getField()[i].getPiece() == Pieces.FLY) {
                System.out.print("F ");
                flyLoc = i;
            } else if(board.getField()[i].getPiece() == Pieces.SPIDER) {
                System.out.print("S ");
                spiderLoc[spiderArrayIndex] = i;
                spiderArrayIndex++;
            } else {
                System.out.print("- ");
            }
        }
        System.out.println("\nPiece objects' locations: ");
        System.out.println("Fly location: " + board.getFly().location);
        for (int i = 0; i < board.getSpider().length; i++) {
            System.out.println("Spider[" + i + "] location: " + board.getSpider()[i].location);
        }

        System.out.println("\nBoard's locations: ");
        System.out.println("Fly found on field number " + flyLoc);
        for (int j : spiderLoc) {
            System.out.println("Spider found on field number " + j);
        }
        System.out.println("\n isFlysTurn: " + board.isFlysTurn() + "\n");
    }

    public boolean newGame(Board board, String gameMode) {
        switch (gameMode) {
            case "pvp": {
                board.setGameMode(PVP);
                //this.board = new Board();
                board.setGameRunning(true);
                board.setPieceWon(0);
                board.setFlysTurn(true);
                board.setFlyStepsDone(0);
                board.setSpiderStepsDone(0);
                return true;
            }
            case "pvs": {
                board.setGameMode(PVS);
                //this.board = new Board();
                board.setGameRunning(true);
                board.setPieceWon(0);
                board.setFlysTurn(true);
                board.setFlyStepsDone(0);
                board.setSpiderStepsDone(0);
                return true;
            }
            case "pvf": {
                board.setGameMode(PVF);
                //this.board = new Board();
                board.setGameRunning(true);
                board.setPieceWon(0);
                board.setFlysTurn(false);
                board.setFlyStepsDone(0);
                board.setSpiderStepsDone(0);
                return true;
            }
            default: return false;
        }
    }

    public boolean getIsGameRunning(Board board) {
        return board.isGameRunning();
    }


    /**
     * pieceWon's value is 0 if no one won yet
     *                      1 if fly won
     *                      2 if spiders won
     * @return value of pieceWon
     */

    public int whoWon(Board board) {
        isGameRunning(board);
        return board.getPieceWon();
    }

    public int getFlyStepsDone(Board board) {
        return board.getFlyStepsDone();
    }

    public int getSpiderStepsDone(Board board) {
        return board.getSpiderStepsDone();
    }





}