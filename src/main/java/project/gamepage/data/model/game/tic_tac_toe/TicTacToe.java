package project.gamepage.data.model.game.tic_tac_toe;

public class TicTacToe {
    private Pieces_TicTacToe[][] board;
    private boolean isXTurn;
    private boolean gameMode; //true = PvP : false = PvC
    private boolean isGameRunning;
    private Pieces_TicTacToe winnerPiece;
    private int X_movesMade;
    private int O_movesMade;


    public TicTacToe() {
        this.board = new Pieces_TicTacToe[3][3];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = Pieces_TicTacToe.EMPTY;
            }
        }

        this.isXTurn = true;
        this.isGameRunning = false;
        this.winnerPiece = Pieces_TicTacToe.EMPTY;
        this.X_movesMade = 0;
        this.O_movesMade = 0;
    }

    public Pieces_TicTacToe[][] cloneBoard() {
        Pieces_TicTacToe[][] clonedBoard = new Pieces_TicTacToe[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                clonedBoard[i][j] = board[i][j];
            }
        }
        return clonedBoard;
    }

    public Pieces_TicTacToe[][] getBoard() {
        return board;
    }

    public void setBoard(Pieces_TicTacToe[][] board) {
        this.board = board;
    }

    public boolean isXTurn() {
        return isXTurn;
    }

    public void setXTurn(boolean XTurn) {
        isXTurn = XTurn;
    }

    public boolean isGameMode() {
        return gameMode;
    }

    public void setGameMode(boolean gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public Pieces_TicTacToe getWinnerPiece() {
        return winnerPiece;
    }

    public void setWinnerPiece(Pieces_TicTacToe winnerPiece) {
        this.winnerPiece = winnerPiece;
    }

    public int getX_movesMade() {
        return X_movesMade;
    }

    public void setX_movesMade(int x_movesMade) {
        X_movesMade = x_movesMade;
    }

    public int getO_movesMade() {
        return O_movesMade;
    }

    public void setO_movesMade(int o_movesMade) {
        O_movesMade = o_movesMade;
    }
}
