# ♞ Chess — play a friend from any device

A two-player online chess game. One person starts a game and shares a link or a 6-character code; the other joins and you play in real time from separate devices. No accounts, no sign-up.

The rules engine is written from scratch in Java (no chess libraries). It is wrapped in a Spring Boot REST API and played through a React UI. The whole thing is deployed on AWS behind CloudFront (HTTPS).

**Live demo:** https://d126mb8yipb4b0.cloudfront.net *(runs on a small EC2 instance; may be offline when I'm not using it)*

<!-- Add a screenshot: save it as docs/screenshot.png and uncomment the line below -->
<!-- ![Chess board](docs/screenshot.png) -->

## Features

- Create a game, share the invite link, and play from two different devices
- Full legal-move validation: you can't leave your own king in check, and pinned pieces can't move
- Check, checkmate and stalemate detection
- Pawn promotion (auto-Queen)
- Click a piece to see its legal moves highlighted; last move and checked king are highlighted
- Each player sees the board from their own side
- Refreshing the page keeps your seat in the game
- Console version of the game still works

## Architecture

```mermaid
flowchart LR
    B["Browser<br/>React UI"] -->|HTTPS| C["CloudFront"]
    C -->|"/ (default)"| S3["S3 bucket<br/>React build, private"]
    C -->|"/api/* (HTTP :80)"| N["nginx<br/>on EC2"]
    N --> S["Spring Boot :8080<br/>managed by systemd"]
    S --> E["ChessGame<br/>hand-written engine"]
```

```
React (board UI)  --polls every 1s-->  Spring Boot REST API  -->  ChessGame (engine, in memory)
```

**Engine layers:** `File/Location` → `Square` → `Board` → `Piece` (Pawn, Rook, Knight, Bishop, Queen, King) → `ChessGame`.
Each piece generates its own pseudo-legal moves; `MoveValidator` then makes each candidate move on the board, checks whether the mover's own king is attacked, and undoes it. Check, checkmate and stalemate all build on that single rule.

**Multiplayer:** the server keeps each game in memory. Joining a game gives you a secret `playerId` (UUID) that the server checks on every move, so only the right player can move on their turn. The browser polls the game state once a second to see the opponent's moves. Each game is guarded by a lock so two requests can't change it at the same time.

## Tech stack

| Layer | Tech |
|---|---|
| Frontend | React 18, Vite |
| Backend | Java 17+, Spring Boot 3.3 (REST) |
| Engine | Plain Java, no dependencies |
| Tests | JUnit 5 |
| Hosting | AWS CloudFront (Free plan), S3 (private bucket), EC2 (Amazon Linux 2023), nginx, systemd |

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
(or in a **private/incognito window**; each browser profile is one "player", so two normal tabs in the same browser will both be the same player).

In development, Vite forwards `/api` calls to `localhost:8080`, so no CORS setup is needed.

Console version: run `com.chess.runner.Game` and enter moves like `E2->E4`.

Tests: `cd backend && mvn test`

## API

| Method | Path | What |
|---|---|---|
| POST | `/api/games` | create a game (you play White) → `{gameId, playerId, color}` |
| POST | `/api/games/{id}/join` | join by code (you play Black) |
| GET | `/api/games/{id}` | board, turn, status (polled by the UI) |
| GET | `/api/games/{id}/moves?from=E2&playerId=…` | legal destinations for a piece |
| POST | `/api/games/{id}/move` | `{playerId, from, to}` |

Game status is one of `WAITING_FOR_OPPONENT`, `IN_PROGRESS`, `CHECKMATE`, `STALEMATE`.
Errors come back as JSON with a `message` (404 unknown game, 403 not a player, 409 not your turn / game full, 400 illegal move).

## Project structure

```
backend/
  src/main/java/com/chess/
    board/ common/ piece/ squares/   the engine (board, pieces, locations)
    game/                            ChessGame (turn, status, promotion), MoveValidator (check logic)
    api/                             GameController, GameService, DTOs, CORS config
    runner/Game.java                 console version
  src/test/java/com/chess/           engine tests
frontend/
  src/App.jsx                        create / join flow
  src/components/GameScreen.jsx      polling + click handling
  src/components/Board.jsx           the board
```

## Tests

Engine tests cover: White moves first, fool's mate, answering a check, a pinned piece that can't move, pawn promotion with a capture, and Sam Loyd's 10-move stalemate (a long game that exercises check and pin logic end to end).

## Deployment (AWS)

```
Browser ──HTTPS──► CloudFront ──default (/)──► S3 bucket        (React build, private)
                       │
                       └──/api/*──HTTP :80──► EC2: nginx ──► Spring Boot :8080 (systemd)
```

- **CloudFront** is the single public entry point and gives the site HTTPS. Because the page and the API share one address, the browser never makes a cross-site call or a mixed-content (HTTPS page → HTTP API) request.
- **S3** holds the React build in a private bucket. CloudFront reads it through Origin Access Control, so the bucket is not public. Default root object: `index.html`.
- **`/api/*` behavior** forwards to the EC2 server (HTTP, port 80) with all HTTP methods allowed, the *CachingDisabled* cache policy (so polling never sees a stale board) and the *AllViewerExceptHostHeader* origin request policy (so query strings like `?from=E2` reach the server).
- **nginx** on EC2 reverse-proxies `/api/` to Spring Boot on the same machine.
- **systemd** runs the Spring Boot jar, restarts it on failure and starts it on boot.
- **EC2:** `t3.micro`-class instance running Amazon Linux 2023 with Amazon Corretto 21.
- **Security group:** SSH (22) from my IP only, HTTP (80) from anywhere (CloudFront connects to it). Port 8080 is not exposed, since nginx reaches Spring Boot on the same machine.

### CORS

Browsers add an `Origin` header to POST requests, and Spring rejects a request whose origin isn't allowed (403 "Invalid CORS request"). The CloudFront address must therefore be in `app.cors.allowed-origins` (in `application.properties`). On the server it can also be set without a rebuild through the `APP_CORS_ALLOWED_ORIGINS` environment variable in a systemd drop-in (`/etc/systemd/system/chess.service.d/cors.conf`).

### Deploy / update

```bash
# build
cd backend  && mvn clean package
cd ../frontend && npm install && npm run build

# backend: upload the jar, restart the service
scp -i ~/.ssh/<key>.pem backend/target/chess-backend-0.0.1.jar ec2-user@<server-ip>:~/chess-backend.jar
ssh -i ~/.ssh/<key>.pem ec2-user@<server-ip> "sudo systemctl restart chess"

# frontend: upload the build to S3, then clear CloudFront's cache
aws s3 sync frontend/dist/ s3://<bucket-name> --delete
aws cloudfront create-invalidation --distribution-id <distribution-id> --paths "/*"
```

(The frontend can also be uploaded from the S3 console; either way, create an invalidation for `/*` afterwards so visitors get the new files.)

Logs: `sudo journalctl -u chess -n 50` (backend) and `sudo journalctl -u nginx -n 50` (nginx).

## Limitations and roadmap

**Rules not implemented yet:** castling, en passant, choosing a promotion piece (always Queen), draw by repetition, draw by insufficient material, resign / draw offers.

**Technical limitations:**
- Games live in memory and are lost when the server restarts
- Traffic is HTTPS from the browser to CloudFront, but plain HTTP from CloudFront to the EC2 server (so it is not encrypted end to end)
- The UI polls every second, and each poll counts as a CloudFront request; WebSockets would be more efficient
- Finished games are not saved

**Ideas:**
- Save finished games (moves, result) in PostgreSQL
- WebSockets (STOMP) instead of polling, or pause polling while the tab is hidden
- Custom domain + certificate so CloudFront can use HTTPS to the origin too
- Castling and en passant
- Move history and a rematch button
- CI/CD with GitHub Actions
