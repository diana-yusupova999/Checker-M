package samp2.sample;

import java.util.Objects;

import static samp2.sample.GameStart.TILE_SIZE;

public class FirstTurn {
    final int oldX;
    final int oldY;
    final int newX;
    final int newY;

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    FirstTurn(Piece pieceForPeople, int newX, int newY) {
        this.oldX = toBoard(pieceForPeople.getOldX());
        this.oldY = toBoard(pieceForPeople.getOldY());
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public String toString() {
        return (oldX+1) + " " + (oldY+1) + "-> " + (newX+1) + " " + (newY+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FirstTurn)) return false;
        FirstTurn firstTurn = (FirstTurn) o;
        return oldX == firstTurn.oldX &&
                oldY == firstTurn.oldY &&
                newX == firstTurn.newX &&
                newY == firstTurn.newY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldX, oldY, newX, newY);
    }
}
