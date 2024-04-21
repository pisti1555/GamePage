package com.example.szakdoga.service;

import com.example.szakdoga.data.model.User;
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

        Game newGame = new Game(username, null, new Board());
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



    //------------------------Game--------------------------
    public int moveVsComputer(int from, int to, Board board) {
        if (whichPiece(from, board) == board.getFly()) {
            moveFly(from, to, board);
            board.flyStepsDone++;
        }
        for (Piece p : board.getSpider()) {
            if (whichPiece(from, board) == p) {
                moveSpider(from, to, p, board);
                board.spiderStepsDone++;
            }
        }

        return whoWon(board);
    }

    public int moveVsPlayer(int from, int to, Board board, String username) {
        Game game = getGame(username);
        if (whichPiece(from, board) == board.getFly()) {
            if (game.getUser1().equals(username)) {
                moveFly(from, to, board);
                board.flyStepsDone++;
            }
        }
        for (Piece p : board.getSpider()) {
            if (whichPiece(from, board) == p) {
                if (game.getUser2().equals(username)) {
                    moveSpider(from, to, p, board);
                    board.spiderStepsDone++;
                }
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

        if (board.isGameRunning) {
            int randomConnection = random.nextInt(availableFields);
            int randomField = board.getField()[board.getFly().location]
                    .getConnection()[randomConnection].getNumber();

            if(isMoveValid(board.getFly().location, randomField, board)) {
                moveFly(board.getFly().location, randomField, board);
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

        if (board.isGameRunning) {
            if(isMoveValid(randomSpider.location, randomField, board)) {
                moveSpider(randomSpider.location, randomField, randomSpider, board);
                return true;
            } else {
                randomMoveSpider(board);
            }
        }
        return false;
    }

    public boolean isMoveValid(int from, int to, Board board) {
        boolean correctPiece = false;
        if(board.isFlysTurn) {
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
        return board.gameMode;
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
    public void moveFly(int from, int to, Board board) {
        if (isMoveValid(from, to, board)) {
            board.getField()[to].setPiece(board.getField()[from].getPiece());
            board.getField()[from].setPiece(Pieces.EMPTY);
            board.getFly().location = to;
            board.isFlysTurn = false;
        } else {
            System.out.println("Invalid");
        }
    }

    public void moveSpider(int from, int to, Piece p, Board board) {
        if (isMoveValid(from, to, board)) {
            board.getField()[to].setPiece(board.getField()[from].getPiece());
            board.getField()[from].setPiece(Pieces.EMPTY);
            p.location = to;
            board.isFlysTurn = true;
        } else {
            System.out.println("Invalid");
        }
    }

    public Piece whichPiece(int location, Board board) {
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
            board.isGameRunning = false;
            board.pieceWon = 1;
        }

        int unavailableFields = 0;
        for (int i = 0; i < board.getField()[board.getFly().location].getConnection().length; i++) {
            if(
                    board.getField()[board.getFly().location].getConnection()[i] == null ||
                            board.getField()[board.getFly().location].getConnection()[i].getPiece() != Pieces.EMPTY
            ) unavailableFields++;
        }

        if (unavailableFields >= board.getField()[board.getFly().location].getConnection().length) {
            board.isGameRunning = false;
            board.pieceWon = 2;
        }
        return board.isGameRunning;
    }

    public boolean isFlysTurn(Board board) {
        return board.isFlysTurn;
    }

    public boolean newGame(String gameMode, Board board, String username) {
        switch (gameMode) {
            case "pvp": {
                board.gameMode = PVP;
                getGame(username).setBoard(new Board());
                board.isGameRunning = true;
                board.pieceWon = 0;
                board.isFlysTurn = true;
                board.flyStepsDone = 0;
                board.spiderStepsDone = 0;
                return true;
            }
            case "pvs": {
                board.gameMode = PVS;
                //this.board = new Board();
                board.isGameRunning = true;
                board.pieceWon = 0;
                board.isFlysTurn = true;
                board.flyStepsDone = 0;
                board.spiderStepsDone = 0;
                return true;
            }
            case "pvf": {
                board.gameMode = PVF;
                //this.board = new Board();
                board.isGameRunning = true;
                board.pieceWon = 0;
                board.isFlysTurn = false;
                board.flyStepsDone = 0;
                board.spiderStepsDone = 0;
                return true;
            }
            default: return false;
        }
    }

    public boolean getIsGameRunning(Board board) {
        return board.isGameRunning;
    }


    /**
     * pieceWon's value is 0 if no one won yet
     *                      1 if fly won
     *                      2 if spiders won
     * @return value of pieceWon
     */
    public int whoWon(Board board) {
        isGameRunning(board);
        return board.pieceWon;
    }

    public int getFlyStepsDone(Board board) {
        return board.flyStepsDone;
    }

    public int getSpiderStepsDone(Board board) {
        return board.spiderStepsDone;
    }


    public void gameOverr(User user, boolean won) {

        if (won) {
            user.setGamesPlayed(user.getGamesPlayed() + 1);
            user.setGamesWon(user.getGamesWon() + 1);
        } else {
            user.setGamesPlayed(user.getGamesPlayed() + 1);
        }
    }

    public void gameOver(Game game, User user) {
        if (game.getUser1().equals(user.getUsername())) {
            if (whoWon(game.getBoard()) == 1) {
                user.setMovesDone(user.getMovesDone() + game.getBoard().flyStepsDone);
                user.setGamesPlayed(user.getGamesPlayed() + 1);
                user.setGamesWon(user.getGamesWon() + 1);
            }
            if (whoWon(game.getBoard()) == 2) {
                user.setMovesDone(user.getMovesDone() + game.getBoard().flyStepsDone);
                user.setGamesPlayed(user.getGamesPlayed() + 1);
            }
        }
        if (game.getUser2().equals(user.getUsername())) {
            if (whoWon(game.getBoard()) == 1) {
                user.setMovesDone(user.getMovesDone() + game.getBoard().spiderStepsDone);
                user.setGamesPlayed(user.getGamesPlayed() + 1);
            }
            if (whoWon(game.getBoard()) == 2) {
                user.setMovesDone(user.getMovesDone() + game.getBoard().spiderStepsDone);
                user.setGamesPlayed(user.getGamesPlayed() + 1);
                user.setGamesWon(user.getGamesWon() + 1);
            }
        }
    }

}