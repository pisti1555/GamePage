package project.gamepage.data.model.game;

public class PvC <Game> {
    String user1;
    String user2;
    Game board;

    public PvC(String user, Game board) {
        this.user1 = user;
        this.user2 = "Computer";
        this.board = board;
    }

    public String getUser() {
        return user1;
    }
    public String getUser2() {return user2;}

    public void setUsername(String user1) {
        this.user1 = user1;
    }
    public void setUser2Name(String user2) {
        this.user2 = user2;
    }

    public Game getBoard() {
        return board;
    }

    public void setBoard(Game board) {
        this.board = board;
    }
}
