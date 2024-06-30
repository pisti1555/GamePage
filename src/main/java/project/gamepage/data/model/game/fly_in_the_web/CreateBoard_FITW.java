package project.gamepage.data.model.game.fly_in_the_web;

public class CreateBoard_FITW {
    private final Field_FITW[] field;
    private final Piece_FITW fly;
    private final Piece_FITW[] spider;

    /**
     * Generating game board, making connections inside it and placing pieces onto it.
     */
    public CreateBoard_FITW() {
        field = new Field_FITW[28];

        for (int i = 0; i < field.length; i++) {
            field[i] = new Field_FITW(Pieces_FITW.EMPTY);
            field[i].setConnection(new Field_FITW[6]);
            field[i].setNumber(i);
        }

        spider = new Piece_FITW[5];
        spider[0] = new Piece_FITW(Pieces_FITW.SPIDER, 0);
        spider[1] = new Piece_FITW(Pieces_FITW.SPIDER, 5);
        spider[2] = new Piece_FITW(Pieces_FITW.SPIDER, 10);
        spider[3] = new Piece_FITW(Pieces_FITW.SPIDER, 14);
        spider[4] = new Piece_FITW(Pieces_FITW.SPIDER, 18);
        fly = new Piece_FITW(Pieces_FITW.FLY, 27);


        field[27].setPiece(Pieces_FITW.FLY);
        field[0].setPiece(Pieces_FITW.SPIDER);
        field[5].setPiece(Pieces_FITW.SPIDER);
        field[10].setPiece(Pieces_FITW.SPIDER);
        field[14].setPiece(Pieces_FITW.SPIDER);
        field[18].setPiece(Pieces_FITW.SPIDER);


        field[0].getConnection()[0] = field[5];
        field[0].getConnection()[1] = field[22];
        field[0].getConnection()[2] = field[6];
        field[0].getConnection()[3] = field[1];

        field[1].getConnection()[0] = field[7];
        field[1].getConnection()[1] = field[23];
        field[1].getConnection()[2] = field[2];
        field[1].getConnection()[3] = field[0];

        field[2].getConnection()[0] = field[7];
        field[2].getConnection()[1] = field[24];
        field[2].getConnection()[2] = field[3];
        field[2].getConnection()[3] = field[1];

        field[3].getConnection()[0] = field[9];
        field[3].getConnection()[1] = field[25];
        field[3].getConnection()[2] = field[4];
        field[3].getConnection()[3] = field[2];

        field[4].getConnection()[0] = field[9];
        field[4].getConnection()[1] = field[26];
        field[4].getConnection()[2] = field[27];
        field[4].getConnection()[3] = field[3];

        field[5].getConnection()[0] = field[0];
        field[5].getConnection()[1] = field[10];
        field[5].getConnection()[2] = field[6];

        field[6].getConnection()[0] = field[0];
        field[6].getConnection()[1] = field[11];
        field[6].getConnection()[2] = field[7];
        field[6].getConnection()[3] = field[5];

        field[7].getConnection()[0] = field[2];
        field[7].getConnection()[1] = field[1];
        field[7].getConnection()[2] = field[11];
        field[7].getConnection()[3] = field[8];
        field[7].getConnection()[4] = field[6];

        field[8].getConnection()[0] = field[12];
        field[8].getConnection()[1] = field[9];
        field[8].getConnection()[2] = field[7];

        field[9].getConnection()[0] = field[4];
        field[9].getConnection()[1] = field[3];
        field[9].getConnection()[2] = field[13];
        field[9].getConnection()[3] = field[27];
        field[9].getConnection()[4] = field[8];

        field[10].getConnection()[0] = field[5];
        field[10].getConnection()[1] = field[14];
        field[10].getConnection()[2] = field[15];
        field[10].getConnection()[3] = field[11];

        field[11].getConnection()[0] = field[6];
        field[11].getConnection()[1] = field[7];
        field[11].getConnection()[2] = field[15];
        field[11].getConnection()[3] = field[12];
        field[11].getConnection()[4] = field[10];

        field[12].getConnection()[0] = field[8];
        field[12].getConnection()[1] = field[16];
        field[12].getConnection()[2] = field[13];
        field[12].getConnection()[3] = field[11];

        field[13].getConnection()[0] = field[9];
        field[13].getConnection()[1] = field[17];
        field[13].getConnection()[2] = field[27];
        field[13].getConnection()[3] = field[12];

        field[14].getConnection()[0] = field[10];
        field[14].getConnection()[1] = field[18];
        field[14].getConnection()[2] = field[19];
        field[14].getConnection()[3] = field[15];

        field[15].getConnection()[0] = field[10];
        field[15].getConnection()[1] = field[11];
        field[15].getConnection()[2] = field[20];
        field[15].getConnection()[3] = field[16];
        field[15].getConnection()[4] = field[14];

        field[16].getConnection()[0] = field[12];
        field[16].getConnection()[1] = field[20];
        field[16].getConnection()[2] = field[21];
        field[16].getConnection()[3] = field[17];
        field[16].getConnection()[4] = field[15];

        field[17].getConnection()[0] = field[13];
        field[17].getConnection()[1] = field[21];
        field[17].getConnection()[2] = field[27];
        field[17].getConnection()[3] = field[16];

        field[18].getConnection()[0] = field[14];
        field[18].getConnection()[1] = field[22];
        field[18].getConnection()[2] = field[24];
        field[18].getConnection()[3] = field[19];

        field[19].getConnection()[0] = field[14];
        field[19].getConnection()[1] = field[24];
        field[19].getConnection()[2] = field[20];
        field[19].getConnection()[3] = field[18];

        field[20].getConnection()[0] = field[15];
        field[20].getConnection()[1] = field[16];
        field[20].getConnection()[2] = field[25];
        field[20].getConnection()[3] = field[21];
        field[20].getConnection()[4] = field[19];

        field[21].getConnection()[0] = field[16];
        field[21].getConnection()[1] = field[17];
        field[21].getConnection()[2] = field[26];
        field[21].getConnection()[3] = field[27];
        field[21].getConnection()[4] = field[20];

        field[22].getConnection()[0] = field[18];
        field[22].getConnection()[1] = field[0];
        field[22].getConnection()[2] = field[23];

        field[23].getConnection()[0] = field[1];
        field[23].getConnection()[1] = field[24];
        field[23].getConnection()[2] = field[22];

        field[24].getConnection()[0] = field[18];
        field[24].getConnection()[1] = field[19];
        field[24].getConnection()[2] = field[2];
        field[24].getConnection()[3] = field[25];
        field[24].getConnection()[4] = field[23];

        field[25].getConnection()[0] = field[20];
        field[25].getConnection()[1] = field[3];
        field[25].getConnection()[2] = field[26];
        field[25].getConnection()[3] = field[24];

        field[26].getConnection()[0] = field[21];
        field[26].getConnection()[1] = field[4];
        field[26].getConnection()[2] = field[27];
        field[26].getConnection()[3] = field[25];

        field[27].getConnection()[0] = field[4];
        field[27].getConnection()[1] = field[9];
        field[27].getConnection()[2] = field[13];
        field[27].getConnection()[3] = field[17];
        field[27].getConnection()[4] = field[21];
        field[27].getConnection()[5] = field[26];
    }

    protected Field_FITW[] giveField() {
        return field;
    }

    protected Piece_FITW giveFly() {
        return fly;
    }

    protected Piece_FITW[] giveSpiders() {
        return spider;
    }
}
