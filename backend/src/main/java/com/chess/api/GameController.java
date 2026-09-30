package com.chess.api;

import com.chess.api.Dtos.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /** Start a new game; the creator plays LIGHT. */
    @PostMapping
    public JoinResponse create() {
        return gameService.createGame();
    }

    /** Join an existing game by its code; the joiner plays DARK. */
    @PostMapping("/{gameId}/join")
    public JoinResponse join(@PathVariable String gameId) {
        return gameService.joinGame(gameId);
    }

    /** Current board, turn and status. The frontend polls this. */
    @GetMapping("/{gameId}")
    public GameStateResponse state(@PathVariable String gameId) {
        return gameService.getState(gameId);
    }

    /** Legal destination squares for the piece on `from` (for highlighting). */
    @GetMapping("/{gameId}/moves")
    public LegalMovesResponse legalMoves(@PathVariable String gameId,
                                         @RequestParam String from,
                                         @RequestParam String playerId) {
        return gameService.getLegalMoves(gameId, playerId, from);
    }

    @PostMapping("/{gameId}/move")
    public GameStateResponse move(@PathVariable String gameId, @RequestBody MoveRequest request) {
        return gameService.move(gameId, request);
    }
}
