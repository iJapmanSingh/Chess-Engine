package com.chess.game;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.piece.AbstractPiece;
import com.chess.piece.King;
import com.chess.piece.PieceColor;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each piece's "pseudo-legal" moves (what getValidMoves returns: the piece's own
 * movement pattern) into truly legal moves: a move is legal only if, after making it,
 * your own king is not attacked. Everything check / checkmate / stalemate builds on this.
 */
public final class MoveValidator {
    private MoveValidator() {
    }

    public static List<Location> getLegalMoves(Board board, AbstractPiece piece) {
        List<Location> legal = new ArrayList<>();
        for (Location target : piece.getValidMoves(board)) {
            if (!leavesOwnKingInCheck(board, piece, board.getSquare(target))) {
                legal.add(target);
            }
        }
        return legal;
    }

    public static boolean hasAnyLegalMove(Board board, PieceColor color) {
        for (AbstractPiece piece : new ArrayList<>(board.getPieces(color))) {
            if (!getLegalMoves(board, piece).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInCheck(Board board, PieceColor color) {
        Location kingLocation = null;
        for (AbstractPiece piece : board.getPieces(color)) {
            if (piece instanceof King) {
                kingLocation = piece.getCurrentSquare().getLocation();
                break;
            }
        }
        if (kingLocation == null) {
            return false;
        }
        PieceColor enemy = opposite(color);
        for (AbstractPiece attacker : new ArrayList<>(board.getPieces(enemy))) {
            if (attacker.getValidMoves(board).contains(kingLocation)) {
                return true;
            }
        }
        return false;
    }

    public static PieceColor opposite(PieceColor color) {
        return color == PieceColor.LIGHT ? PieceColor.DARK : PieceColor.LIGHT;
    }

    // Make the move for real, ask "is my king attacked now?", then put everything back.
    private static boolean leavesOwnKingInCheck(Board board, AbstractPiece piece, Square to) {
        Square from = piece.getCurrentSquare();
        AbstractPiece captured = to.getCurrentPiece();

        if (captured != null) {
            board.removePiece(captured);
        }
        from.reset();
        to.setCurrentPiece(piece);
        to.setOccupied(true);
        piece.setCurrentSquare(to);

        boolean inCheck = isInCheck(board, piece.getPieceColor());

        to.reset();
        if (captured != null) {
            to.setCurrentPiece(captured);
            to.setOccupied(true);
            board.addPiece(captured);
        }
        from.setCurrentPiece(piece);
        from.setOccupied(true);
        piece.setCurrentSquare(from);

        return inCheck;
    }
}
