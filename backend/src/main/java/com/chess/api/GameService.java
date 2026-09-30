package com.chess.api;

import com.chess.api.Dtos.*;
import com.chess.common.File;
import com.chess.common.Location;
import com.chess.game.ChessGame;
import com.chess.game.IllegalMoveException;
import com.chess.piece.AbstractPiece;
import com.chess.piece.PieceColor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps games in memory (lost on restart - fine for a casual game; swap in PostgreSQL later). */
@Service
public class GameService {
    // No 0/O/1/I so codes are easy to read out loud
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();
    private final Map<String, OnlineGame> games = new ConcurrentHashMap<>();

    public JoinResponse createGame() {
        OnlineGame game = new OnlineGame(newCode());
        game.lightPlayerId = UUID.randomUUID().toString();
        games.put(game.id, game);
        return new JoinResponse(game.id, game.lightPlayerId, PieceColor.LIGHT.name());
    }

    public JoinResponse joinGame(String gameId) {
        OnlineGame game = find(gameId);
        synchronized (game) {
            if (game.isFull()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This game already has two players.");
            }
            game.darkPlayerId = UUID.randomUUID().toString();
            return new JoinResponse(game.id, game.darkPlayerId, PieceColor.DARK.name());
        }
    }

    public GameStateResponse getState(String gameId) {
        OnlineGame game = find(gameId);
        synchronized (game) {
            return toState(game);
        }
    }

    public LegalMovesResponse getLegalMoves(String gameId, String playerId, String fromSquare) {
        OnlineGame game = find(gameId);
        synchronized (game) {
            Location from = parse(fromSquare);
            PieceColor player = game.colorOf(playerId);
            List<String> moves = List.of();
            if (player != null && game.isFull() && player == game.game.getTurn()) {
                moves = game.game.getLegalMoves(from).stream().map(GameService::format).toList();
            }
            return new LegalMovesResponse(fromSquare.toUpperCase(), moves);
        }
    }

    public GameStateResponse move(String gameId, MoveRequest request) {
        OnlineGame game = find(gameId);
        synchronized (game) {
            PieceColor player = game.colorOf(request.playerId());
            if (player == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a player in this game.");
            }
            if (!game.isFull()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Waiting for the second player to join.");
            }
            if (player != game.game.getTurn()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "It's not your turn.");
            }
            try {
                game.game.move(parse(request.from()), parse(request.to()));
            } catch (IllegalMoveException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
            return toState(game);
        }
    }

    // ---- helpers ----

    private OnlineGame find(String gameId) {
        OnlineGame game = gameId == null ? null : games.get(gameId.toUpperCase());
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found. Check the code.");
        }
        return game;
    }

    private String newCode() {
        while (true) {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            if (!games.containsKey(code.toString())) {
                return code.toString();
            }
        }
    }

    private GameStateResponse toState(OnlineGame online) {
        ChessGame game = online.game;
        Map<String, PieceDto> pieces = new LinkedHashMap<>();
        for (Map.Entry<Location, AbstractPiece> e : game.getPiecePositions().entrySet()) {
            pieces.put(format(e.getKey()),
                    new PieceDto(e.getValue().getName().toUpperCase(), e.getValue().getPieceColor().name()));
        }
        String status = online.isFull() ? game.getStatus().name() : "WAITING_FOR_OPPONENT";
        return new GameStateResponse(
                online.id,
                status,
                game.getTurn().name(),
                game.isInCheck(),
                game.getWinner() == null ? null : game.getWinner().name(),
                pieces,
                game.getLastFrom() == null ? null : format(game.getLastFrom()),
                game.getLastTo() == null ? null : format(game.getLastTo()),
                game.getMoveCount());
    }

    private static String format(Location location) {
        return location.getFile().name() + location.getRank();
    }

    private static Location parse(String square) {
        try {
            String s = square.trim().toUpperCase();
            int rank = Integer.parseInt(s.substring(1, 2));
            if (s.length() != 2 || rank < 1 || rank > 8) {
                throw new IllegalArgumentException();
            }
            return new Location(File.valueOf(s.substring(0, 1)), rank);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid square: " + square);
        }
    }
}
