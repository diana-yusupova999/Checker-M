package samp2.sample;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static samp2.sample.CheckersApp.gameMain;

public class GameStart {

    public final static int TILE_SIZE = 80; //регулируем окно
    private HashMap<FirstTurn, Pair> turnHashMap = new HashMap<>();
    private FirstTurn firstTurn = null;
    public static final int WIDTH = 8; //ширина
    public static final int HEIGHT = 8; //высота
    private int turn = 1;
    private Tile[][] board = new Tile[WIDTH][HEIGHT]; //доска
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group(); //клеточка доски
    private ArrayList<Piece> pieceList = new ArrayList<>();
    private Rules rules = new RulesForPeople(board, pieceGroup, turn, pieceList);
    private Stage primaryStage;

    public GameStart() {
        if (primaryStage == null) primaryStage = new Stage();
        Scene scene = new Scene(createContent()); /** контейнер графических элементов, созданных в createContent() */
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("CheckersApp"); /** установка заголовка*/
        primaryStage.setResizable(false);
    }

    public int getTurn() {
        return turn;
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) { //расставление фигурок на поле
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new TileForPeople((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add((Node) tile);

                PieceForPeople pieceForPeople = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    pieceForPeople = makePiece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    pieceForPeople = makePiece(PieceType.WHITE, x, y);
                }

                if (pieceForPeople != null) {
                    tile.setPiece(pieceForPeople);
                    pieceGroup.getChildren().add(pieceForPeople); /**для визуализации*/
                    pieceList.add(pieceForPeople); /**основная логическая инфорамация*/
                }

            }
        }
        return root;
    }

    public static int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    public void movePieceII() {
        int x0, y0, newX, newY;

        firstTurn = II.choseFirstTurn(turnHashMap);
        if (firstTurn == null) {
            gameMain = new GameStart();
        }

        System.out.println(turnHashMap);
        System.out.println(firstTurn);

        try {
            x0 = firstTurn.oldX;
            y0 = firstTurn.oldY;
            newX = firstTurn.newX;
            newY = firstTurn.newY;
        } catch (NullPointerException e) {
            return;
        }
        Piece pieceForPeople = board[x0][y0].getPiece();
        MoveResult result;
        if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
            result = new MoveResult(MoveType.NONE);
        } else {
            result = rules.tryMove(pieceForPeople, newX, newY);
        }

        switch (result.getType()) {
            case NONE:
                pieceForPeople.returnMove();
                break;
            case NORMAL:
                pieceForPeople.move(newX, newY);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(pieceForPeople);
                turn *= -1;
                rules.setTurn(turn);
                break;

            case KILL:
                pieceForPeople.move(newX, newY);
                pieceList.remove(board[x0][y0].getPiece());
                pieceList.add(pieceForPeople);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(pieceForPeople);
                Piece otherPieceForPeople = result.getPiece();
                board[toBoard(otherPieceForPeople.getOldX())][toBoard(otherPieceForPeople.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPieceForPeople);
                pieceList.remove(otherPieceForPeople);
                if (rules.mustHackPawn(pieceForPeople) && rules.mustHackKing(pieceForPeople)) {
                    turn *= -1;
                    rules.setTurn(turn);
                } else {
                    turnHashMap = new HashMap<>();
                    try {
                        RulesForII newRules = rules.toII();
                        new II(newRules, turnHashMap).evaluate();
                    } catch (CloneNotSupportedException | IOException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    movePieceII();
                }
                break;
        }
        if (result.getType() != MoveType.NONE && (((newY == HEIGHT - 1) && (pieceForPeople.getType() == PieceType.RED)) || ((newY == 0) && (pieceForPeople.getType() == PieceType.WHITE)))) {
            pieceForPeople.setRank(PieceForPeople.PieceRank.KING);
        }
        boolean flag = false;
        for (Piece piece :
                pieceList) {
            if (piece.getType() == PieceType.WHITE) flag = true;
        }
        if (!flag) { //если игра закончена - начинаем новую
            gameMain = new GameStart();
        }
    }

    private PieceForPeople makePiece(PieceType type, int x, int y) {

        PieceForPeople pieceForPeople = new PieceForPeople(type, x, y);

        pieceForPeople.setOnMouseReleased(e -> {

            int newX = toBoard(pieceForPeople.getLayoutX());
            int newY = toBoard(pieceForPeople.getLayoutY());
            MoveResult result;
            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = rules.tryMove(pieceForPeople, newX, newY);
            }

            int x0 = toBoard(pieceForPeople.getOldX());
            int y0 = toBoard(pieceForPeople.getOldY());
            firstTurn = null;
            turnHashMap = new HashMap<>();
            switch (result.getType()) {
                case NONE:
                    pieceForPeople.returnMove();
                    break;
                case NORMAL: /** обычных ход*/
                    pieceForPeople.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(pieceForPeople);
                    turn *= -1;
                    rules.setTurn(turn);
                    if (turn == -1) {

                        try {
                            RulesForII newRules = rules.toII();
                            new II(newRules, turnHashMap).evaluate();
                        } catch (CloneNotSupportedException | IOException | ClassNotFoundException e1) {
                            e1.printStackTrace();
                        }


                        movePieceII();
                    }
                    break;
                case KILL: /** съедаем */
                    pieceForPeople.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(pieceForPeople);
                    PieceForPeople otherPieceForPeople = (PieceForPeople) result.getPiece();
                    board[toBoard(otherPieceForPeople.getOldX())][toBoard(otherPieceForPeople.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPieceForPeople);
                    pieceList.remove(otherPieceForPeople);
                    if (rules.mustHackPawn(pieceForPeople) && rules.mustHackKing(pieceForPeople)) {
                        turn *= -1;
                        rules.setTurn(turn);
                        if (turn == -1) {
                            try {
                                RulesForII newRules = rules.toII();
                                new II(newRules, turnHashMap).evaluate();
                            } catch (CloneNotSupportedException | IOException | ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            movePieceII();
                        }
                    }
                    break;
            }
            if (result.getType() != MoveType.NONE && (((newY == HEIGHT - 1) && (pieceForPeople.getType() == PieceType.RED)) || ((newY == 0) && (pieceForPeople.getType() == PieceType.WHITE)))) {
                pieceForPeople.setRank(PieceForPeople.PieceRank.KING);
            }
        });
        return pieceForPeople;
    }
}
