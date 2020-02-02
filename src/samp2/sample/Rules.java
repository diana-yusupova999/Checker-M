package samp2.sample;

import javafx.scene.Group;

import java.util.ArrayList;


public abstract class Rules implements Cloneable {

    Tile[][] board;
    Group pieceGroup;
    int turn;
    ArrayList<Piece> pieceList;

    Rules(Tile[][] board, Group pieceGroup, int turn, ArrayList<Piece> pieceList) {
        this.board = board;
        this.pieceGroup = pieceGroup;
        this.turn = turn;
        this.pieceList = pieceList;//список пешек
    }


    public ArrayList<Piece> getPieceList() {
        return pieceList;
    }

    Group getPieceGroup() {
        return pieceGroup;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < GameStart.WIDTH; j++) {
            for (int i = 0; i < GameStart.HEIGHT; i++) {
                if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.RED)
                    stringBuilder.append("1");
                if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.WHITE)
                    stringBuilder.append("2");
                if (!board[i][j].hasPiece()) stringBuilder.append("-");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    @Override
    protected Rules clone() throws CloneNotSupportedException {
        return (Rules) super.clone();
    }

    public RulesForII toII() throws CloneNotSupportedException {
        return new RulesForII(this.clone());
    }


    Tile[][] getBoard() {
        return board;
    }


    public boolean mustHackAllPawn() {
        return (!mustHackAllPawn2().isEmpty());
    }

    public ArrayList<Piece> mustHackAllPawn2() {
        ArrayList<Piece> list = new ArrayList<>();
        for (Piece piece : pieceList) {
            if (piece.getRank() == Piece.PieceRank.PAWN && piece.getType().moveDir != turn && (
                    (MoveType.KILL == tryMove(piece, GameStart.toBoard(piece.getOldX()) - 2, GameStart.toBoard(piece.getOldY()) - 2).getType())
                            || (MoveType.KILL == tryMove(piece, GameStart.toBoard(piece.getOldX()) + 2, GameStart.toBoard(piece.getOldY()) - 2).getType())
                            || (MoveType.KILL == tryMove(piece, GameStart.toBoard(piece.getOldX()) - 2, GameStart.toBoard(piece.getOldY()) + 2).getType())
                            || (MoveType.KILL == tryMove(piece, GameStart.toBoard(piece.getOldX()) + 2, GameStart.toBoard(piece.getOldY()) + 2).getType())))
                list.add(piece);
        }
        return list;
    }


    /**
     * проверка на возможность срубить по всем правилам русских шашек
     */
    MoveResult tryMoveForKill(Piece piece, int newX, int newY) {
        int x0 = GameStart.toBoard(piece.getOldX());
        int y0 = GameStart.toBoard(piece.getOldY());
        if ((newX < 0 || newY < 0 || newX >= GameStart.WIDTH || newY >= GameStart.HEIGHT)
                || board[newX][newY].hasPiece())
            return new MoveResult(MoveType.NONE);
        Piece pieceForKill = null;
        for (int i = Math.min(x0, newX) + 1; i < Math.max(x0, newX); i++)
            for (int j = Math.min(y0, newY) + 1; j < Math.max(y0, newY); j++)
                if (Math.abs(i - x0) == Math.abs(j - y0) && board[i][j].hasPiece()) {
                    if (board[i][j].getPiece().getType() != piece.getType()
                            && pieceForKill == null)
                        pieceForKill = board[i][j].getPiece();
                    else return new MoveResult(MoveType.NONE);
                }
        if (pieceForKill == null) return new MoveResult(MoveType.NORMAL);
        else return new MoveResult(MoveType.KILL, pieceForKill);
    }

    public boolean mustHackAllKing() {
        return mustHackAllKing2() != null;
    }

    /**
     *
     * @return дамка которой можно срубить
     */
    public Piece mustHackAllKing2() {
        for (Piece piece : pieceList)
            if (piece.getRank() == Piece.PieceRank.KING && piece.getType().moveDir != turn)
                for (int k = 2; k < GameStart.HEIGHT; k++) {
                    if (
                            (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) - k, GameStart.toBoard(piece.getOldY()) - k).getType())
                                    || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) + k, GameStart.toBoard(piece.getOldY()) - k).getType())
                                    || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) - k, GameStart.toBoard(piece.getOldY()) + k).getType())
                                    || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) + k, GameStart.toBoard(piece.getOldY()) + k).getType()))
                        return piece;
                }
        return null;
    }


    public boolean mustHackKing(Piece piece) {
        for (int k = 2; k < GameStart.HEIGHT; k++) {
            if (piece.getRank() == Piece.PieceRank.KING && (
                    (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) - k, GameStart.toBoard(piece.getOldY()) - k).getType())
                            || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) + k, GameStart.toBoard(piece.getOldY()) - k).getType())
                            || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) - k, GameStart.toBoard(piece.getOldY()) + k).getType())
                            || (MoveType.KILL == tryMoveForKill(piece, GameStart.toBoard(piece.getOldX()) + k, GameStart.toBoard(piece.getOldY()) + k).getType())))
                return false;
        }
        return true;
    }

    public boolean mustHackPawn(Piece piece) {
        return piece.getRank() != Piece.PieceRank.PAWN || (
                (MoveType.KILL != tryMove(piece, GameStart.toBoard(piece.getOldX()) - 2, GameStart.toBoard(piece.getOldY()) - 2).getType())
                        && (MoveType.KILL != tryMove(piece, GameStart.toBoard(piece.getOldX()) + 2, GameStart.toBoard(piece.getOldY()) - 2).getType())
                        && (MoveType.KILL != tryMove(piece, GameStart.toBoard(piece.getOldX()) - 2, GameStart.toBoard(piece.getOldY()) + 2).getType())
                        && (MoveType.KILL != tryMove(piece, GameStart.toBoard(piece.getOldX()) + 2, GameStart.toBoard(piece.getOldY()) + 2).getType()));
    }

    public MoveResult tryMove(Piece piece, int newX, int newY) {
        return new MoveResult(MoveType.NONE);
    }
}


