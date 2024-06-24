package project.gamepage.data.model.game.spiderweb;

public class Field {
    private Field[] connection;
    private Pieces piece;
    private int number;

    public Field(Pieces piece) {
        this.piece = piece;
    }

    public Field[] getConnection() {
        return connection;
    }

    public void setConnection(Field[] connection) {
        this.connection = connection;
    }

    public Pieces getPiece() {
        return piece;
    }

    public void setPiece(Pieces piece) {
        this.piece = piece;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
