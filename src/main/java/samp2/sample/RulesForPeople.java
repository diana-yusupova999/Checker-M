package samp2.sample;


import javafx.scene.Group;

import java.util.ArrayList;

import static java.lang.Integer.valueOf;

public class RulesForPeople extends Rules implements Cloneable {


    RulesForPeople(Tile[][] board, Group pieceGroup, int turn, ArrayList<Piece> pieceForPeopleList) {
        super(board, pieceGroup, turn, pieceForPeopleList);
    }


    @Override
    protected RulesForPeople clone() throws CloneNotSupportedException {
        RulesForPeople newRules = (RulesForPeople) super.clone();
        newRules.turn = valueOf(turn);
        newRules.board = new Tile[8][8];
        newRules.pieceGroup = new Group();
        newRules.pieceList = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                Tile tile = new TileForPeople();
                if (board[j][i].getPiece() != null) {
                    PieceForPeople newPieceForPeople = new PieceForPeople(board[j][i].getPiece());
                    newPieceForPeople.setRank(board[j][i].getPiece().getRank());
                    tile.setPiece(newPieceForPeople);
                    newRules.pieceGroup.getChildren().add(newPieceForPeople);
                    newRules.pieceList.add(newPieceForPeople);
                }
                newRules.board[j][i] = tile;
            }
        return newRules;
    }


    @Override
    public MoveResult tryMove(Piece pieceForPeople, int newX, int newY) {
        if ((newX < 0 || newY < 0 || newX >= GameStart.WIDTH || newY >= GameStart.HEIGHT) || (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0)) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = GameStart.toBoard(pieceForPeople.getOldX());
        int y0 = GameStart.toBoard(pieceForPeople.getOldY());

        if (pieceForPeople.getRank() == PieceForPeople.PieceRank.PAWN) {


            if (Math.abs(newX - x0) == 1 && newY - y0 == pieceForPeople.getType().moveDir && !mustHackAllPawn() && !mustHackAllKing()) {
                return new MoveResult(MoveType.NORMAL);

            } else if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {

                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != pieceForPeople.getType()) {
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                }
            }

            return new MoveResult(MoveType.NONE);

        } else {
            if (Math.abs(newX - x0) == Math.abs(newY - y0) && !mustHackAllPawn() && !mustHackAllKing()) {
                return new MoveResult(MoveType.NORMAL);
            } else
                return tryMoveForKill(pieceForPeople, newX, newY);
        }
    }
}
