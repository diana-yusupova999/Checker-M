package samp2.sample;

import javafx.scene.Group;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class RulesForPeopleTest {
    //red pawn, white pawn, red tile, white tile
    public static final String RP = "R";
    public static final String WP = "W";
    public static final String RT = "*";
    public static final String WT = "#";

    @Test
    void tryMoveTest() { // проверка результата хода
        assertMoveTypeEquals(1, 2, 2, 3, PieceType.RED, MoveType.NORMAL);
        assertMoveTypeEquals(5, 5, 4, 4, PieceType.WHITE, MoveType.NONE);
        String[][] stringBoard = new String[][]{
                new String[]{WP, WT, WP, WT, WP, WT, WP, WT},
                new String[]{WT, WP, WT, WP, WT, WP, WT, WP},
                new String[]{WT, WP, WT, WP, WT, WP, WT, WP},
                new String[]{RT, WT, RT, WT, RT, WT, RT, WT},
                new String[]{WT, RT, WT, RT, WT, RT, WT, RT},
                new String[]{RT, RP, RT, RP, RT, RP, RT, RP},
                new String[]{RP, RT, RP, RT, RP, RT, RP, RT},
                new String[]{RT, RP, RT, RP, RT, RP, RT, RP},
        };
        assertMoveTypeEquals(stringBoard, 2, 5, 3, 4, PieceType.WHITE, MoveType.NORMAL);
        assertMoveTypeEquals(parseBoardFromFile("test1.txt"), 2, 5, 3, 4, PieceType.RED, MoveType.NONE);
        assertMoveTypeEquals(parseBoardFromFile("test1.txt"), 3, 4, 1, 2, PieceType.WHITE, MoveType.KILL);
    }

    @Test
    public void testEnemyWasKilled(){ // пешка срублена
        ArrayList<Piece> pieceList = new ArrayList<>();
        Rules rules = initDefaultRules(initParsedBoard(parseBoardFromFile("test1.txt"), pieceList), pieceList);
        assertMoveTypeEquals(rules, 3, 4, 1, 2, PieceType.WHITE, MoveType.KILL);
        Assertions.assertFalse(pieceList.contains(new PieceForPeople(PieceType.RED, 2, 3)));
    }

    private void assertMoveTypeEquals(int oldX, int oldY, int newX, int newY, PieceType pieceType, MoveType expectedMoveType) {
        assertMoveTypeEquals(this::initDefaultBoard, oldX, oldY, newX, newY, pieceType, expectedMoveType);
    }

    private void assertMoveTypeEquals(String[][] stringBoard, int oldX, int oldY, int newX, int newY, PieceType pieceType, MoveType expectedMoveType) {
        assertMoveTypeEquals(pieceList -> initParsedBoard(stringBoard, pieceList), oldX, oldY, newX, newY, pieceType, expectedMoveType);
    }

    private void assertMoveTypeEquals(BoardConstructor boardConstructor, int oldX, int oldY, int newX, int newY, PieceType pieceType, MoveType expectedMoveType) {
        ArrayList<Piece> pieceList = new ArrayList<>();
        assertMoveTypeEquals(initDefaultRules(boardConstructor.construct(pieceList), pieceList), oldX, oldY, newX, newY, pieceType, expectedMoveType);
    }

    private void assertMoveTypeEquals(Rules rules, int oldX, int oldY, int newX, int newY, PieceType pieceType, MoveType expectedMoveType) {
        Piece testPawn = new PieceForPeople(pieceType, oldX, oldY);
        MoveResult moveResult = rules.tryMove(testPawn, newX, newY);
        Assertions.assertEquals(expectedMoveType, moveResult.getType());
    }

    private Rules initDefaultRules(Tile[][] board, ArrayList<Piece> pieceList) {
        Group pieceGroup = new Group();
        int turn = 1;
        return new RulesForPeople(board, pieceGroup, turn, pieceList);
    }

    private Tile[][] initDefaultBoard(ArrayList<Piece> pieceList) {
        Tile[][] board = new Tile[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = new TileForPeople((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                PieceForPeople pieceForPeople = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    pieceForPeople = new PieceForPeople(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    pieceForPeople = new PieceForPeople(PieceType.WHITE, x, y);
                }

                if (pieceForPeople != null) {
                    tile.setPiece(pieceForPeople);
                    pieceList.add(pieceForPeople);
                }

            }
        }
        return board;
    }

    private Tile[][] initParsedBoard(String[][] stringBoard, ArrayList<Piece> pieceList) {
        int width = stringBoard.length;
        int height = stringBoard[0].length;
        Tile[][] board = new Tile[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = new TileForPeople((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                PieceForPeople pieceForPeople = null;
                String stringCell = stringBoard[x][y];
                System.out.print(stringCell + "_" + x + ":" + y + " ");
                switch (stringCell) {
                    case RP: {
                        pieceForPeople = new PieceForPeople(PieceType.RED, x, y);
                        break;
                    }
                    case WP: {
                        pieceForPeople = new PieceForPeople(PieceType.WHITE, x, y);
                        break;
                    }

                }
                if (pieceForPeople != null) {
                    tile.setPiece(pieceForPeople);
                    pieceList.add(pieceForPeople);
                }
            }
            System.out.println();
        }
        System.out.println();
        return board;
    }

    private String[][] parseBoard(String string) {
        String[] lines = string.split("\n");
        String[][] result = new String[lines.length][lines[0].split("").length];
        for (int y = 0; y < lines.length; y++) {
            String[] columns = lines[y].split("");
            for (int x = 0; x < columns.length; x++) {
                result[x][y] = columns[x];
            }
        }
        return result;
    }

    private String[][] parseBoardFromFile(String fileName) {
        String resources = new File("").getAbsolutePath() + "/src/test/resources/";
        File file = new File(resources + fileName);
        StringBuilder sb = new StringBuilder();
        try {
            for (String line : Files.readAllLines(file.toPath())) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseBoard(sb.toString());
    }

    private interface BoardConstructor {
        Tile[][] construct(ArrayList<Piece> pieceList);
    }
}
