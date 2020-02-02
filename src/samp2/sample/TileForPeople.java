package samp2.sample;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class TileForPeople extends Rectangle implements Cloneable, Tile {

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

    public TileForPeople(boolean light, int x, int y) {
        setWidth(GameStart.TILE_SIZE);
        setHeight(GameStart.TILE_SIZE);
        relocate(x * GameStart.TILE_SIZE, y * GameStart.TILE_SIZE);
        setFill(light ? Color.valueOf("#feb") : Color.valueOf("#582"));
    }

    TileForPeople() {
    }
}

