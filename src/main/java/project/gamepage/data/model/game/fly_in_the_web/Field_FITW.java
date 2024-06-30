package project.gamepage.data.model.game.fly_in_the_web;

public class Field_FITW {
    private Field_FITW[] connection;
    private Pieces_FITW piece;
    private int number;

    public Field_FITW(Pieces_FITW piece) {
        this.piece = piece;
    }

    public Field_FITW[] getConnection() {
        return connection;
    }

    public void setConnection(Field_FITW[] connection) {
        this.connection = connection;
    }

    public Pieces_FITW getPiece() {
        return piece;
    }

    public void setPiece(Pieces_FITW piece) {
        this.piece = piece;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
