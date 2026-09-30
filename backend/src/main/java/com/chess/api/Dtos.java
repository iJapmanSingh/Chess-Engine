package com.chess.api;

import java.util.List;
import java.util.Map;

/** Request/response shapes for the REST API. */
public final class Dtos {
    private Dtos() {
    }

    /** Returned when you create or join a game. Keep playerId secret - it proves which player you are. */
    public record JoinResponse(String gameId, String playerId, String color) {
    }

    public record PieceDto(String type, String color) {
    }

    public record MoveRequest(String playerId, String from, String to) {
    }

    public record GameStateResponse(
            String gameId,
            String status,          // WAITING_FOR_OPPONENT | IN_PROGRESS | CHECKMATE | STALEMATE
            String turn,            // LIGHT | DARK
            boolean check,
            String winner,          // LIGHT | DARK | null
            Map<String, PieceDto> pieces,   // "E2" -> PAWN/LIGHT
            String lastFrom,
            String lastTo,
            int moveCount) {
    }

    public record LegalMovesResponse(String from, List<String> moves) {
    }
}
