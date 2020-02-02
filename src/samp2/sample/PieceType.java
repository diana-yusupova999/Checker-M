package samp2.sample;


public enum PieceType implements Cloneable {
    RED(1), WHITE(-1);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }

}