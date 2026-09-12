package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class King extends AbstractPiece implements Movable {

    private Bishop bishop;
    private Rook rook;

    public King(PieceColor pieceColor, Bishop bishop, Rook rook) {
        super(pieceColor);
        this.name = "King";
        this.bishop = bishop;
        this.rook = rook;
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        List<Location> moveCandidates = new ArrayList<>();

        moveCandidates.addAll(rook.getValidMoves(board, this.getCurrentSquare()));
        moveCandidates.addAll(bishop.getValidMoves(board, this.getCurrentSquare()));

        Location current = this.getCurrentSquare().getLocation();

        return moveCandidates.stream()
                .filter(candidate ->
                        Math.abs(candidate.getFile().ordinal() - current.getFile().ordinal()) <= 1 &&
                                Math.abs(candidate.getRank() - current.getRank()) <= 1)
                .collect(Collectors.toList());
    }

    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        return List.of();
    }
}
