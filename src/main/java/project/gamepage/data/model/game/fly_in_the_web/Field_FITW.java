package project.gamepage.data.model.game.fly_in_the_web;

import java.util.ArrayList;

public class Field_FITW {
    private ArrayList<Field_FITW> connections;
    private Pieces_FITW piece;
    private int number;

    public Field_FITW(Pieces_FITW piece) {
        this.piece = piece;
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

    public ArrayList<Field_FITW> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Field_FITW> connections) {
        this.connections = connections;
    }
}
