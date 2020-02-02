package samp2.sample;


public interface Piece {

    enum PieceRank {
        PAWN, KING
    }

    PieceType getType();

    double getOldX();

    double getOldY();

    void setRank(PieceRank newRank);

    PieceRank getRank();

    void move(int x, int y);

    void returnMove();
}
