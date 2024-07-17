package project.gamepage.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.ai.tic_tac_toe.AI_TicTacToe;
import project.gamepage.data.model.game.stats.TicTacToeStats;
import project.gamepage.data.model.game.tic_tac_toe.Pieces_TicTacToe;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.data.model.user.User;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameService_TicTacToe {
    private final List<PvP<TicTacToe>> pvpList;
    private final List<PvC<TicTacToe>> pvcList;
    private final UserService userService;
    private final InvitationService invitationService;
    private final GameStatsService gameStatsService;
    private final Random random;

    @Autowired
    public GameService_TicTacToe(InvitationService invitationService, UserService userService, GameStatsService gameStatsService) {
        this.invitationService = invitationService;
        this.userService = userService;
        this.gameStatsService = gameStatsService;
        this.pvpList = new ArrayList<>();
        this.pvcList = new ArrayList<>();
        this.random = new Random();
    }

    public PvP<TicTacToe> getPvP(String username) {
        for (PvP<TicTacToe> pvp : pvpList) {
            if (pvp.getUser1().equals(username) || (pvp.getUser2() != null && pvp.getUser2().equals(username))) {
                return pvp;
            }
        }

        return createPvP(username);
    }

    public PvC<TicTacToe> getPvC(String username) {
        for (PvC<TicTacToe> pvc : pvcList) {
            if (pvc.getUser().equals(username)) {
                return pvc;
            }
        }

        return createPvC(username);
    }

    public PvP<TicTacToe> createPvP(String username) {
        PvP<TicTacToe> newPvP = new PvP<>(username, null, new TicTacToe());
        pvpList.add(newPvP);
        return newPvP;
    }

    public PvC<TicTacToe> createPvC(String username) {
        PvC<TicTacToe> newPvC = new PvC<>(username, new TicTacToe());
        pvcList.add(newPvC);
        return newPvC;
    }

    public PvP<TicTacToe> joinLobby(String inviter, String invited, String game) {
        for (PvP<TicTacToe> pvp : pvpList) {
            if (pvp.getUser1().equals(inviter)) {
                pvpList.removeIf(i -> i.getUser1().equals(invited));
                pvp.setUser2(invited);
                invitationService.removeInvitation(invited, inviter, game);
                return pvp;
            }
        }
        return null;
    }

    public PvP<TicTacToe> quitLobby(String name) {
        PvP<TicTacToe> pvp = getPvP(name);
        if (pvp.getUser1() != null && pvp.getUser2() != null) {
            pvpList.remove(pvp);
        }
        return getPvP(name);
    }



    //-------------------------------Game----------------------------------

    public boolean move(int row, int col, TicTacToe game, Pieces_TicTacToe piece) {
        if (isMoveValid(row, col, piece, game)) {
            game.getBoard()[row][col] = piece;
            game.setXTurn(!game.isXTurn());
            if (piece.equals(Pieces_TicTacToe.X)) {
                game.setX_movesMade(game.getX_movesMade() + 1);
            } else {
                game.setO_movesMade(game.getO_movesMade() + 1);
            }
            return isSomebodyWon(game);
        }
        return false;
    }

    public boolean moveAI(int row, int col, TicTacToe game, Pieces_TicTacToe piece) {
        if (isMoveValid(row, col, piece, game)) {
            game.getBoard()[row][col] = piece;
            if (isSomebodyWon(game)) return isSomebodyWon(game);
            game.setXTurn(!game.isXTurn());
            AI_TicTacToe ai = new AI_TicTacToe(game.cloneBoard(), piece);
            int aiRow = ai.getBestMove()[0];
            int aiCol = ai.getBestMove()[1];

            if (piece.equals(Pieces_TicTacToe.X)) {
                move(aiRow, aiCol, game, Pieces_TicTacToe.O);
            } else {
                move(aiRow, aiCol, game, Pieces_TicTacToe.X);
            }
        }
        return false;
    }

    private List<int[]> getFreeFields(TicTacToe game) {
        List<int[]> fields = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (game.getBoard()[i][j].equals(Pieces_TicTacToe.EMPTY)) {
                    fields.add(new int[] {i, j});
                }
            }
        }

        return fields;
    }

    public short whichWon(TicTacToe game) {
        boolean isThereFreeSpace = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (game.getBoard()[i][j].equals(Pieces_TicTacToe.EMPTY)) {
                    isThereFreeSpace = true;
                }
            }
        }

        if (!isThereFreeSpace && game.getWinnerPiece().equals(Pieces_TicTacToe.EMPTY)) return 3;
        if (game.getWinnerPiece().equals(Pieces_TicTacToe.X)) return 1;
        if (game.getWinnerPiece().equals(Pieces_TicTacToe.O)) return 2;
        return 0;
    }

    private boolean isMoveValid(int row, int col, Pieces_TicTacToe piece, TicTacToe game) {
        if (isSomebodyWon(game)) return false;
        if (game.getBoard()[row][col] == Pieces_TicTacToe.EMPTY) {
            if (piece.equals(Pieces_TicTacToe.X) && game.isXTurn()) {
                return true;
            }
            if (piece.equals(Pieces_TicTacToe.O) && !game.isXTurn()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSomebodyWon(TicTacToe game) {
        Pieces_TicTacToe[][] board = game.getBoard();
        Pieces_TicTacToe X = Pieces_TicTacToe.X;
        Pieces_TicTacToe O = Pieces_TicTacToe.O;
        if (
                board[0][0].equals(X) && board[0][1].equals(X) && board[0][2].equals(X) ||
                board[1][0].equals(X) && board[1][1].equals(X) && board[1][2].equals(X) ||
                board[2][0].equals(X) && board[2][1].equals(X) && board[2][2].equals(X) ||
                board[0][0].equals(X) && board[1][0].equals(X) && board[2][0].equals(X) ||
                board[0][1].equals(X) && board[1][1].equals(X) && board[2][1].equals(X) ||
                board[0][2].equals(X) && board[1][2].equals(X) && board[2][2].equals(X) ||
                board[0][0].equals(X) && board[1][1].equals(X) && board[2][2].equals(X) ||
                board[0][2].equals(X) && board[1][1].equals(X) && board[2][0].equals(X)
        ) {
            game.setGameRunning(false);
            game.setWinnerPiece(X);
            return true;
        }
        if (
                board[0][0].equals(O) && board[0][1].equals(O) && board[0][2].equals(O) ||
                board[1][0].equals(O) && board[1][1].equals(O) && board[1][2].equals(O) ||
                board[2][0].equals(O) && board[2][1].equals(O) && board[2][2].equals(O) ||
                board[0][0].equals(O) && board[1][0].equals(O) && board[2][0].equals(O) ||
                board[0][1].equals(O) && board[1][1].equals(O) && board[2][1].equals(O) ||
                board[0][2].equals(O) && board[1][2].equals(O) && board[2][2].equals(O) ||
                board[0][0].equals(O) && board[1][1].equals(O) && board[2][2].equals(O) ||
                board[0][2].equals(O) && board[1][1].equals(O) && board[2][0].equals(O)
        ) {
            game.setGameRunning(false);
            game.setWinnerPiece(O);
            return true;
        }

        return false;
    }

    public boolean newGamePvC(String gameMode, String username) {
        PvC<TicTacToe> pvc = getPvC(username);
        switch (gameMode) {
            case "x": {
                pvc.setBoard(new TicTacToe());
                pvc.getBoard().setGameMode(false);
                pvc.getBoard().setGameRunning(true);
                pvc.getBoard().setWinnerPiece(Pieces_TicTacToe.EMPTY);
                pvc.getBoard().setXTurn(true);
                return true;
            }
            case "o": {
                pvc.setBoard(new TicTacToe());
                pvc.getBoard().setGameMode(false);
                pvc.getBoard().setGameRunning(true);
                pvc.getBoard().setWinnerPiece(Pieces_TicTacToe.EMPTY);
                pvc.getBoard().setXTurn(false);
                return true;
            }
            default: return false;
        }
    }

    public int gameOver(PvP<TicTacToe> pvp) {
        TicTacToe game = pvp.getBoard();
        if (pvp.isOver()) return whichWon(game);
        User user1 = userService.findByUsername(pvp.getUser1());
        User user2 = userService.findByUsername(pvp.getUser2());
        TicTacToeStats user1Stats = user1.getTicTacToeStats();
        TicTacToeStats user2Stats = user2.getTicTacToeStats();

        if (whichWon(game) == 1) {
            if (pvp.getPrimaryPiece() == 1) {
                user1Stats.setMovesMade(user1Stats.getMovesMade() + pvp.getBoard().getX_movesMade());
                user1Stats.setGamesPlayed(user1Stats.getGamesPlayed() + 1);
                user1Stats.setGamesWon(user1Stats.getGamesWon() + 1);

                user2Stats.setMovesMade(user2Stats.getMovesMade() + pvp.getBoard().getO_movesMade());
                user2Stats.setGamesPlayed(user2Stats.getGamesPlayed() + 1);
            } else {
                user2Stats.setMovesMade(user2Stats.getMovesMade() + pvp.getBoard().getX_movesMade());
                user2Stats.setGamesPlayed(user2Stats.getGamesPlayed() + 1);
                user2Stats.setGamesWon(user2Stats.getGamesWon() + 1);

                user1Stats.setMovesMade(user1Stats.getMovesMade() + pvp.getBoard().getO_movesMade());
                user1Stats.setGamesPlayed(user1Stats.getGamesPlayed() + 1);
            }
        } else if (whichWon(game) == 2) {
            if (pvp.getSecondaryPiece() == 2) {
                user2Stats.setMovesMade(user2Stats.getMovesMade() + pvp.getBoard().getO_movesMade());
                user2Stats.setGamesPlayed(user2Stats.getGamesPlayed() + 1);
                user2Stats.setGamesWon(user2Stats.getGamesWon() + 1);

                user1Stats.setMovesMade(user1Stats.getMovesMade() + pvp.getBoard().getX_movesMade());
                user1Stats.setGamesPlayed(user1Stats.getGamesPlayed() + 1);
            } else {
                user1Stats.setMovesMade(user1Stats.getMovesMade() + pvp.getBoard().getX_movesMade());
                user1Stats.setGamesPlayed(user1Stats.getGamesPlayed() + 1);
                user1Stats.setGamesWon(user1Stats.getGamesWon() + 1);

                user2Stats.setMovesMade(user2Stats.getMovesMade() + pvp.getBoard().getO_movesMade());
                user2Stats.setGamesPlayed(user2Stats.getGamesPlayed() + 1);
            }
        }

        gameStatsService.saveTicTacToe(user1.getTicTacToeStats());
        gameStatsService.saveTicTacToe(user2.getTicTacToeStats());
        pvp.setOver(true);
        return whichWon(game);
    }
}
