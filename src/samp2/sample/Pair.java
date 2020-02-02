package samp2.sample;

import java.util.Objects;

public class Pair {
    private int PieceMy;
    private int PieceOther;

    Pair(int pieceMy, int pieceOther) {
        PieceMy = pieceMy;
        PieceOther = pieceOther;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return PieceMy == pair.PieceMy &&
                PieceOther == pair.PieceOther;
    }

    @Override
    public int hashCode() {

        return Objects.hash(PieceMy, PieceOther);
    }

    @Override
    public String toString() {
        if (PieceMy != 0) return PieceOther * 100 / PieceMy + "%  ";
        else return "goodTurn";
    }

    public int getPieceMy() {
        return PieceMy;
    }

    public void setPieceMy(int pieceMy) {
        PieceMy = pieceMy;
    }

    public int getPieceOther() {
        return PieceOther;
    }

    public void setPieceOther(int pieceOther) {
        PieceOther = pieceOther;
    }
}
