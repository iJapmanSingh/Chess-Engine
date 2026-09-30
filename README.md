# Chess — play a friend from any device

React frontend + Spring Boot API + your hand-written Java chess engine.

```
React (board UI)  --polls every 1s-->  Spring Boot REST API  -->  ChessGame (your engine, in memory)
```

## Run locally

Needs JDK 17+, Maven and Node 18+.

```bash
# terminal 1 - API on :8080
cd backend
mvn spring-boot:run

# terminal 2 - UI on :5173
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 → **Start a new game** → copy the invite link → open it on another device
(or in a **private/incognito window** — each browser profile is one "player", so two normal tabs in the same
browser will both be the same player).

Console version still works: run `com.chess.runner.Game`.

Tests: `cd backend && mvn test`

## API

| Method | Path | What |
|---|---|---|
| POST | `/api/games` | create a game (you play White) → `{gameId, playerId, color}` |
| POST | `/api/games/{id}/join` | join by code (you play Black) |
| GET | `/api/games/{id}` | board, turn, status (polled by the UI) |
| GET | `/api/games/{id}/moves?from=E2&playerId=…` | legal destinations for a piece |
| POST | `/api/games/{id}/move` | `{playerId, from, to}` |

`playerId` is a secret UUID that proves who you are; the server checks it on every move.

## Rules implemented

Legal-move filtering (can't leave your king in check; pins work), check, checkmate, stalemate,
pawn promotion (auto-Queen). **Not yet:** castling, en passant, draw by repetition / insufficient material.

## Where things live

- `backend/src/main/java/com/chess/game/` — `ChessGame` (turn, status, promotion), `MoveValidator` (check logic)
- `backend/src/main/java/com/chess/api/` — `GameController`, `GameService`, DTOs, CORS
- `frontend/src/` — `App.jsx` (create/join), `components/GameScreen.jsx` (polling + clicks), `components/Board.jsx`

## Production notes

- Frontend: `npm run build`, set `VITE_API_URL` to your API URL first (see `.env.example`).
- Backend: set `app.cors.allowed-origins` to your frontend's URL.
- Games live in memory and are lost on restart.
