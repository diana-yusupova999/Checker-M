package samp2.sample;


import javafx.scene.Group;

import java.util.ArrayList;

import static java.lang.Integer.valueOf;

public class RulesForII extends Rules {

    public RulesForII(Tile[][] board, Group pieceGroup, int turn, ArrayList<Piece> pieceList) {
        super(board, pieceGroup, turn, pieceList);
    }

    RulesForII(Rules other) {
        this(other.getBoard(), other.getPieceGroup(), other.getTurn(), other.getPieceList());
    }

    @Override
    public Group getPieceGroup() {
        return pieceGroup;
    }

    @Override
    protected RulesForII clone() throws CloneNotSupportedException {
        RulesForII newRules = (RulesForII) super.clone();
        newRules.turn = valueOf(turn);
        newRules.board = new Tile[8][8];
        newRules.pieceGroup = new Group();
        newRules.pieceList = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                Tile tile = new TileForII();
                if (board[j][i].getPiece() != null) {
                    Piece newPiece = new PieceForII(board[j][i].getPiece());
                    newPiece.setRank(board[j][i].getPiece().getRank());
                    tile.setPiece(newPiece);
                    newRules.pieceList.add(newPiece);
                }
                newRules.board[j][i] = tile;
            }
        return newRules;
    }

    @Override
    public MoveResult tryMove(Piece piece, int newX, int newY) {
        if ((newX < 0 || newY < 0 || newX >= GameStart.WIDTH || newY >= GameStart.HEIGHT) || board[newX][newY].hasPiece()) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = GameStart.toBoard(piece.getOldX());
        int y0 = GameStart.toBoard(piece.getOldY());

        if (piece.getRank() == Piece.PieceRank.PAWN) {
            if (Math.abs(newX - x0) == 1) {

                return new MoveResult(MoveType.NORMAL);
            } else if (Math.abs(newX - x0) == 2) {
                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;
                if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                }
            }
            return new MoveResult(MoveType.NONE);
        } else {
            return tryMoveForKill(piece, newX, newY);
        }
    }

    public void makePiece(PieceType type, int newX, int newY, int x0, int y0) {
        Piece piece = new PieceForII(type, x0, y0);
        MoveResult result;
        if (newX < 0 || newY < 0 || newX >= GameStart.WIDTH || newY >= GameStart.HEIGHT) {
            result = new MoveResult(MoveType.NONE);
        } else {
            if (board[x0][y0].hasPiece()) piece.setRank(board[x0][y0].getPiece().getRank());
            result = tryMove(piece, newX, newY);
        }

        switch (result.getType()) {
            case NONE:
                piece.returnMove();
                break;
            case NORMAL:
                piece.move(newX, newY);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(piece);
                turn *= -1;
                break;
            case KILL:
                piece.move(newX, newY);
                pieceList.remove(board[x0][y0].getPiece());
                pieceList.add(piece);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(piece);
                PieceForII otherPiece = (PieceForII) result.getPiece();
                board[GameStart.toBoard(otherPiece.getOldX())][GameStart.toBoard(otherPiece.getOldY())].setPiece(null);
                pieceList.remove(otherPiece);
                if (mustHackPawn(piece) && mustHackKing(piece))
                    turn *= -1;
                break;
        }
        if (result.getType() != MoveType.NONE && (((newY == GameStart.HEIGHT - 1) && (piece.getType() == PieceType.RED)) || ((newY == 0) && (piece.getType() == PieceType.WHITE)))) {
            piece.setRank(Piece.PieceRank.KING);
        }
    }
}
