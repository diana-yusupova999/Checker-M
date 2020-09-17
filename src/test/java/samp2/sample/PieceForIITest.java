package samp2.sample;

import static samp2.sample.GameStart.TILE_SIZE;

public class PieceForIITest implements Piece {

    PieceForIITest(Piece piece) {
        this(piece.getType(), toBoard(piece.getOldX()), toBoard(piece.getOldY()));
        this.setRank(piece.getRank());
    }

    private static int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private final PieceType type;
    private PieceRank rank;
    private double oldX, oldY;
    private int x, y;

    public PieceType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public void setRank(PieceRank newRank) {
        rank = newRank;
    }

    @Override
    protected PieceForIITest clone() throws CloneNotSupportedException {
        PieceForIITest newPieceForII = (PieceForIITest) super.clone();
        if (rank == PieceRank.PAWN) newPieceForII.setRank(PieceRank.PAWN);
        else newPieceForII.setRank(PieceRank.KING);
        return newPieceForII;
    }

    @Override
    public String toString() {
        return "type=" + type + ", rank=" + rank + ", x=" + x + ", y=" + y;
    }

    public PieceRank getRank() {
        return rank;
    }

    public double getOldY() {
        return oldY;
    }


    public PieceForIITest(PieceType type, int x, int y) {
        this.type = type;
        this.y = y;
        this.x = x;
        rank = PieceRank.PAWN;
        move(x, y);
    }

    public void move(int x, int y) {
        this.oldX = x * TILE_SIZE;
        this.oldY = y * TILE_SIZE;
        this.y = y;
        this.x = x;
    }

    public void returnMove() {
        move(toBoard(oldX), toBoard(oldY));
    }
}
