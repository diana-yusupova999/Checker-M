package samp2.sample;

import com.sun.istack.internal.Nullable;
//import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.*;

import static samp2.sample.GameStart.toBoard;
import static samp2.sample.Piece.PieceRank.PAWN;

public class II {
    private int turn;
    private int depth;
    private int pieceMy;
    private int pieceOther;
    private Rules rules;
    private FirstTurn firstTurn;
    private static final int depthII = 5;
    private HashMap<FirstTurn, Pair> turnHashMap;

    private II(FirstTurn firstTurn, int depth, int pieceMy, int pieceOther, Rules rules, HashMap<FirstTurn, Pair> turnHashMap) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        this.turn = rules.getTurn();
        this.depth = depth;
        this.pieceMy = pieceMy;
        this.pieceOther = pieceOther;
        this.firstTurn = firstTurn;
        this.rules = rules;
        this.turnHashMap = turnHashMap;
        if (depth == 1) {
            turnHashMap.put(firstTurn, new Pair(pieceMy, pieceOther));
        }
        if (depth >= 1) {
            Pair pair = turnHashMap.get(firstTurn);
            pair.setPieceMy(pair.getPieceMy() + pieceMy);
            pair.setPieceOther(pair.getPieceOther() + pieceOther);
        }
         if (firstTurn == null) firstTurn = null;
    }

    public void evaluate() throws CloneNotSupportedException, IOException, ClassNotFoundException {
        if (depth < depthII)
            if (rules.mustHackAllPawn() || rules.mustHackAllKing())
                killHim();
            else normalTurn();
    }

    @Nullable
    static FirstTurn choseFirstTurn(HashMap<FirstTurn, Pair> turnHashMap) {
        if (turnHashMap.isEmpty()) return null;
        Set<Map.Entry<FirstTurn, Pair>> set = turnHashMap.entrySet();//карта ходов
        Double goodTurnPercent = -10000000.0;
        FirstTurn goodTurn = null;
        for (Map.Entry<FirstTurn, Pair> me : set) {
            if (me.getValue().getPieceMy() == 0) return me.getKey();
            if (((double) me.getValue().getPieceOther()) / me.getValue().getPieceMy() > goodTurnPercent) {
                goodTurnPercent = ((double) me.getValue().getPieceOther()) / me.getValue().getPieceMy();
                goodTurn = me.getKey();
            }
        }
        return goodTurn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pieceOther).append(" ").append(depth).append(" ").append(pieceMy).append("  ").append(turn).append("\n");
        if (firstTurn != null) sb.append(firstTurn).append("\n");
        sb.append(rules);
        return sb.toString();
    }

    public II(int depth, int pieceMy, int pieceOther, Rules rules, HashMap<FirstTurn, Pair> turnHashMap) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        this(null, depth, pieceMy, pieceOther, rules, turnHashMap);
    }

    public II(Rules rules, HashMap<FirstTurn, Pair> turnHashMap) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        this(null, 0, 0, 0, rules, turnHashMap);
    }

    private void killHim() throws CloneNotSupportedException, IOException, ClassNotFoundException {
        if (rules.mustHackAllKing()) {
            Piece piece = rules.mustHackAllKing2();
            killHim(piece);
        }
        if (rules.mustHackAllPawn()) {
            for (Piece piece : rules.mustHackAllPawn2()) {
                killHim(piece);
            }
        }
    }

    private void anyTurn(Piece piece, int x, int y) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        MoveType moveType = rules.tryMove(piece, toBoard(piece.getOldX()) + x, toBoard(piece.getOldY()) + y).getType();
        if (MoveType.NONE != moveType) {
            RulesForII newRules = rules.toII();
            FirstTurn newFirstTurn;
            if (this.firstTurn == null) {
                newFirstTurn = new FirstTurn(piece, toBoard(piece.getOldX()) + x, toBoard(piece.getOldY()) + y);
            } else newFirstTurn = this.firstTurn;

            newRules.makePiece(piece.getType(), toBoard(piece.getOldX()) + x, toBoard(piece.getOldY()) + y,
                    toBoard(piece.getOldX()), toBoard(piece.getOldY()));

            if (moveType == MoveType.KILL) {
                if (turn == 1) new II(newFirstTurn, depth, pieceMy + 1, pieceOther, newRules, turnHashMap).evaluate();
                else
                    new II(newFirstTurn, depth, pieceMy, pieceOther + 1, newRules, turnHashMap).evaluate();
            } else if (!rules.mustHackAllKing())
                new II(newFirstTurn, depth + 1, pieceMy, pieceOther, newRules, turnHashMap).evaluate();
        }
    }

    private void killHim(Piece piece) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        if (piece.getRank() == PAWN) {
            if (depth == 0) depth++;
            anyTurn(piece, 2, 2);
            anyTurn(piece, 2, -2);
            anyTurn(piece, -2, 2);
            anyTurn(piece, -2, -2);
        } else {
            for (int k = 2; k < GameStart.HEIGHT; k++) {
                if (depth == 0) depth++;
                anyTurn(piece, k, k);
                anyTurn(piece, k, -k);
                anyTurn(piece, -k, k);
                anyTurn(piece, -k, -k);
            }
        }
    }

    private void normalTurn() throws CloneNotSupportedException, IOException, ClassNotFoundException {
        for (Piece piece : rules.getPieceList())
            if (piece.getType().moveDir != turn)

                if (piece.getRank() == PAWN) {
                    anyTurn(piece, 1, piece.getType().moveDir);
                    anyTurn(piece, -1, piece.getType().moveDir);
                } else

                    for (int k = 1; k < GameStart.HEIGHT; k++) {
                        anyTurn(piece, k, k);
                        anyTurn(piece, k, -k);
                        anyTurn(piece, -k, k);
                        anyTurn(piece, -k, -k);
                    }
    }
}
