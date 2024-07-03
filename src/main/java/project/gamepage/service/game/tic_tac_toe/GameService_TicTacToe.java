package project.gamepage.service.game.tic_tac_toe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.ai.tic_tac_toe.AI_TicTacToe;
import project.gamepage.data.model.game.tic_tac_toe.Pieces_TicTacToe;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.invitations.GameInvitation;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class GameService_TicTacToe {
    private final List<PvP<TicTacToe>> pvpList;
    private final List<PvC<TicTacToe>> pvcList;
    private final UserService userService;
    private final InvitationService invitationService;
    private final Random random;

    @Autowired
    public GameService_TicTacToe(InvitationService invitationService, UserService userService) {
        this.invitationService = invitationService;
        this.userService = userService;
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

                /*
                for (Map.Entry<String, GameInvitation> entry : invitationService.invites.entrySet()) {
                    String invitedUser = entry.getKey();
                    String inviterUser = entry.getValue().getInviter();
                    String gameName = entry.getValue().getGame();
                    if (invited.equals(invitedUser)  && inviter.equals(inviterUser) && game.equals(gameName)) {
                        invitationService.invites.remove(invitedUser);
                    }
                }

                 */

                invitationService.removeInvitation(invited, inviter, game);

                return pvp;
            }
        }
        return null;
    }

    public PvP<TicTacToe> quitLobby(String name) {
        PvP<TicTacToe> pvp = getPvP(name);
        pvpList.remove(pvp);
        return pvp;
    }



    //-------------------------------Game----------------------------------

    public boolean move(int row, int col, TicTacToe game, Pieces_TicTacToe piece) {
        if (isMoveValid(row, col, piece, game)) {
            game.getBoard()[row][col] = piece;
            game.setXTurn(!game.isXTurn());
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
                board[0][0].equals(O) && board[0][1].equals(O) && board[0][2].equals(O)
                || board[0][0].equals(O) && board[1][0].equals(O) && board[2][0].equals(O)
                || board[0][2].equals(O) && board[1][2].equals(O) && board[2][2].equals(O)
                || board[2][0].equals(O) && board[2][1].equals(O) && board[2][2].equals(O)
                || board[0][2].equals(O) && board[1][1].equals(O) && board[2][0].equals(O)
                || board[0][0].equals(O) && board[1][1].equals(O) && board[2][2].equals(O)
        ) {
            game.setGameRunning(false);
            game.setWinnerPiece(O);
            return true;
        }

        return false;
    }


}
