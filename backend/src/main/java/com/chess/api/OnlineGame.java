package com.chess.api;

import com.chess.game.ChessGame;
import com.chess.piece.PieceColor;

/** A ChessGame plus the two players sitting at it. */
class OnlineGame {
    final String id;
    final ChessGame game = new ChessGame();
    String lightPlayerId;
    String darkPlayerId;

    OnlineGame(String id) {
        this.id = id;
    }

    boolean isFull() {
        return lightPlayerId != null && darkPlayerId != null;
    }

    PieceColor colorOf(String playerId) {
        if (playerId != null && playerId.equals(lightPlayerId)) {
            return PieceColor.LIGHT;
        }
        if (playerId != null && playerId.equals(darkPlayerId)) {
            return PieceColor.DARK;
        }
        return null;
    }
}
