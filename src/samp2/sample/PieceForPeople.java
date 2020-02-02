package samp2.sample;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static samp2.sample.CheckersApp.gameMain;
import static samp2.sample.GameStart.TILE_SIZE;


public class PieceForPeople extends StackPane implements Cloneable, Piece {

    PieceForPeople(Piece piece) {
        this(piece.getType(), toBoard(piece.getOldX()), toBoard(piece.getOldY()));
        this.setRank(piece.getRank());
    }

    private static int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private final PieceType type;
    private PieceRank rank;
    private double mouseX, mouseY;
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
        if (rank == PieceRank.KING) {
            this.setEffect(new DropShadow(50, Color.BLUE));//подсвет клетки
        }
    }

    @Override
    protected PieceForPeople clone() throws CloneNotSupportedException {
        PieceForPeople newPieceForPeople = (PieceForPeople) super.clone();
        if (rank == PieceRank.PAWN) newPieceForPeople.setRank(PieceRank.PAWN);
        else newPieceForPeople.setRank(PieceRank.KING);
        return newPieceForPeople;
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


    PieceForPeople(PieceType type, int x, int y) {
        this.type = type;
        this.y = y;
        this.x = x;
        rank = PieceRank.PAWN;
        move(x, y);

        Ellipse bg = new Ellipse(GameStart.TILE_SIZE * 0.3125, GameStart.TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(GameStart.TILE_SIZE * 0.03);

        bg.setTranslateX((GameStart.TILE_SIZE - GameStart.TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((GameStart.TILE_SIZE - GameStart.TILE_SIZE * 0.26 * 2) / 2 + GameStart.TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(GameStart.TILE_SIZE * 0.3125, GameStart.TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.RED
                ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(GameStart.TILE_SIZE * 0.03);

        ellipse.setTranslateX((GameStart.TILE_SIZE - GameStart.TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((GameStart.TILE_SIZE - GameStart.TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            if (((gameMain.getTurn() == 1) && (type == PieceType.WHITE)) || (((gameMain.getTurn() == -1) && (type == PieceType.RED))))//проверка на то кто ходит
                relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y) {
        oldX = x * GameStart.TILE_SIZE;
        oldY = y * GameStart.TILE_SIZE;
        this.y = y;
        this.x = x;
        relocate(oldX, oldY);
    }

    public void returnMove() {
        relocate(oldX, oldY);
    }
}