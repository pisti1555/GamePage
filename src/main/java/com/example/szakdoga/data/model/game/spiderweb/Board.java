package com.example.szakdoga.data.model.game.spiderweb;

public class Board {
    private Integer id;
    private final Field[] field;
    private final Piece fly;
    private final Piece[] spider;

    public Board() {
        CreateBoard cb = new CreateBoard();
        field = cb.giveField();
        fly = cb.giveFly();
        spider = cb.giveSpiders();
    }

    public Field[] getField() {
        return field;
    }

    public Piece getFly() {
        return fly;
    }

    public Piece[] getSpider() {
        return spider;
    }
}
