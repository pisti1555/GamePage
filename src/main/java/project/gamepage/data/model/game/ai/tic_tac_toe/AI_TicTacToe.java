package project.gamepage.data.model.game.ai.tic_tac_toe;

import project.gamepage.data.model.game.tic_tac_toe.Pieces_TicTacToe;

public class AI_TicTacToe {
    private final Pieces_TicTacToe User_Piece;
    private final Pieces_TicTacToe AI_Piece;
    private final int[] bestMove;

    public AI_TicTacToe(Pieces_TicTacToe[][] board, Pieces_TicTacToe user) {
        this.User_Piece = user;
        if (user.equals(Pieces_TicTacToe.X)) this.AI_Piece = Pieces_TicTacToe.O; else this.AI_Piece = Pieces_TicTacToe.X;
        this.bestMove = new int[2];
        bestMove(board);
    }

    private void bestMove(Pieces_TicTacToe[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1;
        int moveCol = -1;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].equals(Pieces_TicTacToe.EMPTY)) {
                    board[i][j] = AI_Piece;
                    int score = minimax(board, 0, false);
                    board[i][j] = Pieces_TicTacToe.EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        moveRow = i;
                        moveCol = j;
                    }
                }
            }
        }
        bestMove[0] = moveRow;
        bestMove[1] = moveCol;
    }

    private int minimax(Pieces_TicTacToe[][] board, int depth, boolean isMax) {
        AI_TicTacToe_Outcomes result = checkWinner(board);
        if (result != AI_TicTacToe_Outcomes.ONGOING) {
            if (result == AI_TicTacToe_Outcomes.X) return -10 + depth;
            if (result == AI_TicTacToe_Outcomes.O) return 10 - depth;
            if (result == AI_TicTacToe_Outcomes.DRAW) return 0;
        }
        if (isMax) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j].equals(Pieces_TicTacToe.EMPTY)) {
                        board[i][j] = AI_Piece;
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = Pieces_TicTacToe.EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j].equals(Pieces_TicTacToe.EMPTY)) {
                        board[i][j] = User_Piece;
                        int score = minimax(board, depth + 1, true);
                        board[i][j] = Pieces_TicTacToe.EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private AI_TicTacToe_Outcomes checkWinner(Pieces_TicTacToe[][] board) {
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
        ) return AI_TicTacToe_Outcomes.X;
        if (
                board[0][0].equals(O) && board[0][1].equals(O) && board[0][2].equals(O)
                        || board[0][0].equals(O) && board[1][0].equals(O) && board[2][0].equals(O)
                        || board[0][2].equals(O) && board[1][2].equals(O) && board[2][2].equals(O)
                        || board[2][0].equals(O) && board[2][1].equals(O) && board[2][2].equals(O)
                        || board[0][2].equals(O) && board[1][1].equals(O) && board[2][0].equals(O)
                        || board[0][0].equals(O) && board[1][1].equals(O) && board[2][2].equals(O)
        ) return AI_TicTacToe_Outcomes.O;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == Pieces_TicTacToe.EMPTY) {
                    return AI_TicTacToe_Outcomes.ONGOING;
                }
            }
        }
        return AI_TicTacToe_Outcomes.DRAW;
    }

    public int[] getBestMove() {
        return bestMove;
    }
}
