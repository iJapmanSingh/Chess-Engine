package com.chess;

import com.chess.common.File;
import com.chess.common.Location;
import com.chess.game.ChessGame;
import com.chess.game.GameStatus;
import com.chess.game.IllegalMoveException;
import com.chess.piece.AbstractPiece;
import com.chess.piece.PieceColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {

    private static Location loc(String s) {
        return new Location(File.valueOf(s.substring(0, 1).toUpperCase()), Integer.parseInt(s.substring(1, 2)));
    }

    /** Plays moves written like "e2e4 e7e5 ...". */
    private static ChessGame play(String moves) {
        ChessGame game = new ChessGame();
        for (String m : moves.trim().split("\\s+")) {
            game.move(loc(m.substring(0, 2)), loc(m.substring(2, 4)));
        }
        return game;
    }

    @Test
    void lightMovesFirst() {
        assertEquals(PieceColor.LIGHT, new ChessGame().getTurn());
        assertThrows(IllegalMoveException.class, () -> play("e7e5"));
    }

    @Test
    void foolsMateIsCheckmate() {
        ChessGame game = play("f2f3 e7e5 g2g4 d8h4");
        assertEquals(GameStatus.CHECKMATE, game.getStatus());
        assertEquals(PieceColor.DARK, game.getWinner());
        assertTrue(game.isInCheck());
        assertThrows(IllegalMoveException.class, () -> game.move(loc("a2"), loc("a3")));
    }

    @Test
    void mustAnswerCheck() {
        ChessGame game = play("e2e4 d7d5 f1b5");
        assertTrue(game.isInCheck());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertThrows(IllegalMoveException.class, () -> game.move(loc("a7"), loc("a6")));
        game.move(loc("c7"), loc("c6")); // blocks the check
        assertFalse(game.isInCheck());
    }

    @Test
    void pinnedPieceCannotMove() {
        // Black bishop on b4 pins the white knight on c3 to the king on e1
        ChessGame game = play("d2d4 e7e6 b1c3 f8b4");
        assertTrue(game.getLegalMoves(loc("c3")).isEmpty(), "Knight on c3 is pinned to the king");
    }

    @Test
    void stalemateIsADraw() {
        // Sam Loyd's 10-move stalemate
        ChessGame game = play("e2e3 a7a5 d1h5 a8a6 h5a5 h7h5 h2h4 a6h6 a5c7 f7f6 "
                + "c7d7 e8f7 d7b7 d8d3 b7b8 d3h7 b8c8 f7g6 c8e6");
        assertEquals(GameStatus.STALEMATE, game.getStatus());
        assertFalse(game.isInCheck());
        assertNull(game.getWinner());
    }

    @Test
    void pawnPromotesToQueenAndCaptureRemovesPiece() {
        ChessGame game = play("h2h4 g7g5 h4g5 a7a6 g5g6 a6a5 g6h7 a5a4 h7g8");
        AbstractPiece onG8 = game.getPiecePositions().get(loc("g8"));
        assertEquals("Queen", onG8.getName());
        assertEquals(PieceColor.LIGHT, onG8.getPieceColor());
        assertEquals(13, game.getBoard().getPieces(PieceColor.DARK).size()); // lost g-pawn, h-pawn, knight
    }
}
