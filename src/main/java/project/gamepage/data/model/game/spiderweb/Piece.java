package project.gamepage.data.model.game.spiderweb;

public class Piece {
    public Pieces type;
    public int location;

    public Piece(Pieces type, int location) {
        this.type = type;
        this.location = location;
    }
}
