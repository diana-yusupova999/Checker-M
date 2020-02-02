package samp2.sample;


public class TileForII implements Cloneable, Tile {
    private Piece piece;

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public TileForII() {
    }
}
