package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;

public class Queen extends AbstractPiece implements Movable {

    private Movable bishop;
    private Movable rook;

    public Queen(PieceColor pieceColor, Movable bishop, Movable rook) {
        super(pieceColor);
        this.name = "Queen";
        this.bishop = bishop;
        this.rook = rook;
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        List<Location> moveCandidates = new ArrayList<>();
        moveCandidates.addAll(bishop.getValidMoves(board, this.getCurrentSquare()));
        moveCandidates.addAll(rook.getValidMoves(board, this.getCurrentSquare()));
        return moveCandidates;
    }

    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        return List.of();
    }
}
