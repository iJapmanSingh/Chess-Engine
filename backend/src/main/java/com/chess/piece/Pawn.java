package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.common.LocationFactory;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pawn extends AbstractPiece implements Movable {

    public Pawn(PieceColor pieceColor) {
        super(pieceColor);
        this.name = "Pawn";
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        Location current = this.getCurrentSquare().getLocation();
        Map<Location, Square> squareMap = board.getLocationSquareMap();
        // LIGHT pawns move toward higher ranks, DARK pawns toward lower ranks.
        int direction = this.getPieceColor().equals(PieceColor.LIGHT) ? 1 : -1;

        List<Location> moveCandidates = new ArrayList<>();

        Location oneStep = LocationFactory.build(current, 0, direction);
        if (oneStep != null && squareMap.containsKey(oneStep) && !squareMap.get(oneStep).isOccupied()) {
            moveCandidates.add(oneStep);
            // Start rank is derived from position (not a flag) so the engine can
            // simulate a move and undo it without corrupting pawn state.
            boolean onStartRank = current.getRank() == (direction == 1 ? 2 : 7);
            if (onStartRank) {
                Location twoStep = LocationFactory.build(current, 0, 2 * direction);
                if (twoStep != null && squareMap.containsKey(twoStep) && !squareMap.get(twoStep).isOccupied()) {
                    moveCandidates.add(twoStep);
                }
            }
        }

        for (int fileOffset : new int[]{-1, 1}) {
            Location diagonal = LocationFactory.build(current, fileOffset, direction);
            if (diagonal == null || !squareMap.containsKey(diagonal)) {
                continue;
            }
            Square diagonalSquare = squareMap.get(diagonal);
            if (diagonalSquare.isOccupied()
                    && !diagonalSquare.getCurrentPiece().getPieceColor().equals(this.getPieceColor())) {
                moveCandidates.add(diagonal);
            }
        }

        return moveCandidates;
    }

    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        return List.of();
    }
}
