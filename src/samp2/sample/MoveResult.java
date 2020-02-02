package samp2.sample;


public class MoveResult {

    private final MoveType type;

    public MoveType getType() {
        return type;
    }

    private final Piece piece;

    public Piece getPiece() {
        return piece;
    }

    MoveResult(MoveType type) {
        this(type, null);
    }

    MoveResult(MoveType type, Piece piece) {
        this.type = type;
        this.piece = piece;
    }
}