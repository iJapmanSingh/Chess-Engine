# ♟️ Chess Engine

A Java-based chess engine built from scratch to practice **Object-Oriented Programming (OOP)**, clean code organization, and domain modeling — with a working two-player move loop, not just a static board.

This project focuses on the underlying structure and rules engine of chess rather than a graphical interface.

## 🎯 Purpose

I built this project to strengthen my understanding of Java and Object-Oriented Programming by modeling a real-world system made of multiple interacting objects — and to practice designing a system from the ground up instead of jumping straight to writing methods.

The project helped me practice:

- Object-Oriented Programming
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Interfaces
- Composition
- Java Collections
- Package organization
- Designing relationships between objects
- Bottom-up system design

## 🧱 How I Approached the Design (Bottom-Up)

Instead of starting from "Game" and working downward, I built this by first identifying the smallest, most indivisible facts about a chess position, and only combined them into bigger objects once each layer worked:

1. **File** and **Rank** — a column (A–H) and a row (1–8). The most basic facts you can state about a square; nothing simpler exists.
2. **Location** — a `File` + `Rank` combined into one coordinate. The first compound concept: two primitives become one meaningful unit.
3. **Square** — a `Location` plus board-specific state: its color, whether it's occupied, and which piece (if any) sits on it. This is where pure geometry turns into game state.
4. **Board** — a collection of `Square`s, plus the lookup (`Map<Location, Square>`) that lets any piece answer "what's around me?"
5. **Piece** (and its six subclasses) — sit on top of `Square` and `Board`, using them to answer "where can I legally move from here?"
6. **Game** — the orchestration layer on top of everything: whose turn it is, reading input, and applying a move once a piece confirms it's valid.

Each layer only depends on the layer(s) directly below it — a `Piece` doesn't care how `Square` stores its color, and `Game` doesn't care how a `Piece` calculates its moves. That separation is what let me rework how pieces validate their moves later without touching `Board` or `Square` at all. That was the real lesson of this project: identifying the smallest indivisible concept in a domain and building upward, instead of trying to design the "whole system" top-down on day one.

## 🏗️ Project Structure

```
com.chess
├── board
│   └── Board
├── common
│   ├── File
│   ├── Location
│   └── LocationFactory
├── piece
│   ├── AbstractPiece
│   ├── Movable (interface)
│   ├── PieceColor
│   ├── PieceFactory
│   └── Pawn / Rook / Knight / Bishop / Queen / King
├── runner
│   └── Game
└── squares
    ├── Square
    └── SquareColor
```

### Main Components

**File / Location** — the coordinate system: a column + row pair identifying a position on the board.

**Square** — an individual square: its color, and whether/which piece occupies it.

**Board** — owns all 64 squares and the `Location → Square` map every piece uses to look around itself.

**Piece** (`AbstractPiece` + `Movable`) — shared piece behavior (name, color, current square) with each subclass implementing its own move-generation rules. `King` and `Queen` compose a `Bishop` and `Rook` internally to reuse sliding-move logic instead of duplicating it.

**Game** — the entry point: reads console input, tracks whose turn it is, asks the selected piece for its valid moves, and applies the move if it's legal.

## 🧠 OOP Concepts Used

### Encapsulation
Objects manage their own state and expose only the operations that are necessary.

### Abstraction
Common chess-piece behavior is represented through `AbstractPiece` and the `Movable` interface, so each piece only has to implement what makes it different.

### Inheritance
Every piece extends `AbstractPiece` and shares its state handling and default move-application logic.

### Polymorphism
`Game` treats every piece through its common abstraction (`piece.getValidMoves(board)`) without knowing or caring which specific piece it's talking to.

### Composition
`King` and `Queen` are composed of a `Bishop` and a `Rook` internally, reusing their sliding-move logic instead of re-implementing diagonal/straight-line movement from scratch. `Board` is composed of `Square`s, and each `Square` is tied to a `Location`.

## 🛠️ Tech Stack

- **Java** (plain `javac` / `java` — no build tool or external dependencies)
- **Java Collections** (`HashMap`, `List`, `Stream`)

## 🚀 Getting Started

### Prerequisites

- JDK 8 or later

### Clone the repository

```bash
git clone https://github.com/iJapmanSingh/Chess-Engine.git
cd Chess-Engine
```

### Compile and run

```bash
javac -d out $(find src -name "*.java")
java -cp out com.chess.runner.Game
```

> On Windows, run those two commands from Git Bash/WSL, or just open the project in an IDE (IntelliJ/Eclipse) and run `src/com/chess/runner/Game.java` directly.

### How to play

The game runs in the console and alternates turns, starting with LIGHT. Enter moves in the format `<from>-><to>`:

```
LIGHT to move (e.g. E2->E4): E2->E4
DARK to move (e.g. E7->E5): E7->E5
```

Each move is checked against that piece's own movement rules and against whose turn it is — illegal moves are rejected with a message instead of being applied.

## 📌 Current Status

- [x] Board / square / location model
- [x] Move generation for all six piece types (sliding pieces, knight jumps, direction- and color-aware pawn logic)
- [x] Turn-based game loop with per-piece move validation
- [x] Basic invalid-input handling
- [ ] Check detection
- [ ] Checkmate / stalemate detection
- [ ] Castling
- [ ] En passant
- [ ] Pawn promotion
- [ ] Move history / notation
- [ ] Game state persistence (save/load)
- [ ] Automated tests
- [ ] Graphical or web interface

> Note: "legal" currently means a move matches the piece's own movement pattern and it's that color's turn — the engine doesn't yet reason about check, so a move that leaves your own king in check is still allowed for now.

## 📚 What I Learned

Building this project helped me understand that designing software is different from just writing methods. Working through it bottom-up — starting from `File`/`Rank` and only combining upward once each layer actually worked — made me think about:

- What is the smallest fact I can state about this domain, and can I model it as its own type?
- What objects exist, and what should each one be responsible for?
- Which classes should know about each other, and which shouldn't?
- What behavior belongs in the shared parent, and what's genuinely piece-specific?
- Where should state actually live, so I only have to change one place when something's wrong?

Chess was a good domain for this because it has clearly defined entities and relationships — it forced me to practice identifying the "atoms" of a system before assembling them, rather than starting with one big `Game` class and hoping the structure would emerge on its own.

## 🔮 Future Direction

The long-term goal is to evolve this beyond a console-based engine into a full application:

```
        Chess Application
               │
        Spring Boot API
               │
      ┌────────┴────────┐
      │                 │
Chess Engine        PostgreSQL
      │
Game Logic
      │
Move Validation
```

This would allow the project to eventually include user accounts, game history, persistence, APIs, and a frontend.

## 👨‍💻 Author

**Japman Singh**

This project was created as part of my journey toward becoming a software engineer, with a focus on Java, backend development, and strong computer science fundamentals.
