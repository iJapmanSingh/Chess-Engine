package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.squares.Square;

import java.util.List;

public abstract class AbstractPiece implements Movable {
    protected String name;
    protected PieceColor pieceColor;
    protected Square currentSquare;

    public AbstractPiece(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public String getName() {
        return name;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public Square getCurrentSquare() {
        return currentSquare;
    }

    public void setCurrentSquare(Square currentSquare) {
        this.currentSquare = currentSquare;
    }

    @Override
    public String toString() {
        return "AbstractPiece{" +
                "name='" + name + '\'' +
                ", pieceColor=" + pieceColor +
                ", currentSquare=" + currentSquare +
                '}';
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        return List.of();
    }

    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        return List.of();
    }

    // Shared move-application logic: clear the old square, occupy the new
    // one. Every concrete piece used to duplicate (or stub out) this; now
    // they only override it when they need extra bookkeeping (see Pawn).
    @Override
    public void makeMoves(Square square) {
        Square current = this.getCurrentSquare();
        if (current != null) {
            current.reset();
        }
        this.setCurrentSquare(square);
        square.setCurrentPiece(this);
        square.setOccupied(true);
    }
}
