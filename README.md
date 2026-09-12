# ♟️ Chess Engine

A Java-based chess engine built from scratch to practice and apply **Object-Oriented Programming (OOP)** principles, clean code organization, and domain modeling.

This project focuses on building the underlying structure of a chess game rather than creating a graphical interface.

## 🎯 Purpose

I built this project to strengthen my understanding of Java and Object-Oriented Programming by modeling a real-world system with multiple interacting objects.

The project helped me practice:

* Object-Oriented Programming
* Encapsulation
* Abstraction
* Inheritance
* Polymorphism
* Interfaces
* Composition
* Java Collections
* Package organization
* Designing relationships between objects

## 🏗️ Project Structure

The project is organized around the main components of a chess game:

```text
com.chess
├── board
│   └── Board
├── common
│   ├── File
│   └── Location
├── piece
│   ├── AbstractPiece
│   └── ...
├── runner
│   └── Game
└── squares
    ├── Square
    └── SquareColor
```

### Main Components

**Board**

Responsible for representing the chess board and maintaining the relationship between locations and squares.

**Square**

Represents an individual square on the chess board.

**Location**

Represents a position on the board using coordinates.

**Piece**

Represents chess pieces and their shared behavior using object-oriented abstractions.

**Game**

Acts as the entry point for running the application.

## 🧠 OOP Concepts Used

### Encapsulation

Objects manage their own state and expose only the operations that are necessary.

### Abstraction

Common chess-piece behavior is represented through abstractions so individual pieces can provide their own implementations.

### Inheritance

Specific chess pieces can share common functionality through a common base abstraction.

### Polymorphism

Different chess pieces can be treated through their common abstraction while maintaining their individual behavior.

### Composition

The board is composed of squares and locations, allowing the system to model the relationships between different parts of a chess game.

## 🛠️ Tech Stack

* **Java**
* **Object-Oriented Programming**
* **Java Collections**
* **Maven** *(if applicable)*

## 🚀 Getting Started

### Prerequisites

Make sure you have installed:

* Java JDK
* Maven *(if using Maven)*

### Clone the repository

```bash
git clone <your-repository-url>
cd chess-engine
```

### Run the project

Run the `Game` class from:

```text
src/main/java/com/chess/runner/Game.java
```

The application currently runs through the console.

## 📌 Current Status

The project is currently focused on the **core chess domain model and OOP architecture**.

Planned improvements include:

* [ ] Complete movement validation
* [ ] Turn management
* [ ] Legal move validation
* [ ] Check detection
* [ ] Checkmate detection
* [ ] Castling
* [ ] En passant
* [ ] Pawn promotion
* [ ] Move history
* [ ] Game state management
* [ ] Automated tests
* [ ] Graphical or web interface

## 📚 What I Learned

Building this project helped me understand that designing software is different from simply writing code.

Instead of thinking only about individual methods, I had to think about:

* What objects exist?
* What responsibilities should each object have?
* Which classes should know about each other?
* What behavior belongs to the parent abstraction?
* Where should state be stored?
* How can the design remain extensible?

Chess was useful as a project because it has clearly defined entities and relationships, making it a good domain for practicing object-oriented design.

## 🔮 Future Direction

The long-term goal is to evolve this project beyond a console-based chess engine into a complete application.

Potential future architecture:

```text
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
