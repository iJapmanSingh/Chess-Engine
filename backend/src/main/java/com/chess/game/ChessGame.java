package com.chess.game;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.piece.*;
import com.chess.squares.Square;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One chess game: the board, whose turn it is, and whether the game is over.
 * Used by both the console runner and the REST API, so the rules live in one place.
 */
public class ChessGame {
    private final Board board = new Board();
    private PieceColor turn = PieceColor.LIGHT;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private PieceColor winner;
    private boolean inCheck;
    private Location lastFrom;
    private Location lastTo;
    private int moveCount;

    public Board getBoard() {
        return board;
    }

    public PieceColor getTurn() {
        return turn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PieceColor getWinner() {
        return winner;
    }

    public boolean isInCheck() {
        return inCheck;
    }

    public Location getLastFrom() {
        return lastFrom;
    }

    public Location getLastTo() {
        return lastTo;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public Map<Location, AbstractPiece> getPiecePositions() {
        Map<Location, AbstractPiece> positions = new LinkedHashMap<>();
        for (Square square : board.getLocationSquareMap().values()) {
            if (square.isOccupied()) {
                positions.put(square.getLocation(), square.getCurrentPiece());
            }
        }
        return positions;
    }

    /** Legal destinations for the piece on `from` (empty if no piece, wrong turn, or game over). */
    public List<Location> getLegalMoves(Location from) {
        Square square = board.getSquare(from);
        if (status != GameStatus.IN_PROGRESS || square == null || !square.isOccupied()) {
            return List.of();
        }
        AbstractPiece piece = square.getCurrentPiece();
        if (piece.getPieceColor() != turn) {
            return List.of();
        }
        return MoveValidator.getLegalMoves(board, piece);
    }

    public void move(Location from, Location to) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalMoveException("The game is over.");
        }
        Square fromSq = board.getSquare(from);
        Square toSq = board.getSquare(to);
        if (fromSq == null || toSq == null) {
            throw new IllegalMoveException("That square is off the board.");
        }
        AbstractPiece piece = fromSq.getCurrentPiece();
        if (piece == null) {
            throw new IllegalMoveException("There's no piece on that square.");
        }
        if (piece.getPieceColor() != turn) {
            throw new IllegalMoveException("It's " + turn + "'s turn.");
        }
        if (!getLegalMoves(from).contains(to)) {
            throw new IllegalMoveException(piece.getName() + " can't move there.");
        }

        AbstractPiece captured = toSq.getCurrentPiece();
        if (captured != null) {
            board.removePiece(captured);
        }
        piece.makeMoves(toSq);
        promoteIfNeeded(piece, toSq);

        lastFrom = from;
        lastTo = to;
        moveCount++;
        turn = MoveValidator.opposite(turn);
        updateStatus();
    }

    // Pawn reaching the last rank becomes a Queen (keeps the UI simple; no piece picker).
    private void promoteIfNeeded(AbstractPiece piece, Square square) {
        if (!(piece instanceof Pawn)) {
            return;
        }
        int lastRank = piece.getPieceColor() == PieceColor.LIGHT ? 8 : 1;
        if (square.getLocation().getRank() != lastRank) {
            return;
        }
        PieceColor color = piece.getPieceColor();
        Queen queen = new Queen(color, new Bishop(color), new Rook(color));
        board.removePiece(piece);
        board.addPiece(queen);
        square.setCurrentPiece(queen);
        queen.setCurrentSquare(square);
    }

    // After a move, `turn` is the player who must respond.
    private void updateStatus() {
        inCheck = MoveValidator.isInCheck(board, turn);
        if (!MoveValidator.hasAnyLegalMove(board, turn)) {
            if (inCheck) {
                status = GameStatus.CHECKMATE;
                winner = MoveValidator.opposite(turn);
            } else {
                status = GameStatus.STALEMATE;
            }
        }
    }
}
