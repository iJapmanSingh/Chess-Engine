package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.common.LocationFactory;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rook extends AbstractPiece implements Movable {

    public Rook(PieceColor pieceColor) {
        super(pieceColor);
        this.name = "Rook";
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        return getValidMoves(board, this.getCurrentSquare());
    }

    // Takes an explicit square (rather than always using this.getCurrentSquare())
    // so King/Queen can reuse this sliding logic from their own square without
    // this Rook actually being placed there.
    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        List<Location> moveCandidates = new ArrayList<>();
        Map<Location, Square> squareMap = board.getLocationSquareMap();
        Location current = square.getLocation();
        // Left and right
        getFileCandidates(moveCandidates, squareMap, current, -1);
        getFileCandidates(moveCandidates, squareMap, current, 1);
        // Up and down
        getRankCandidates(moveCandidates, squareMap, current, 1);
        getRankCandidates(moveCandidates, squareMap, current, -1);
        return moveCandidates;
    }

    private void getFileCandidates(
            List<Location> moveCandidates,
            Map<Location, Square> squareMap,
            Location current,
            int offset) {

        Location next = LocationFactory.build(current, offset, 0);
        while (next != null && squareMap.containsKey(next)) {
            Square square = squareMap.get(next);
            if (square.isOccupied()) {
                if (square.getCurrentPiece().getPieceColor().equals(this.getPieceColor())) {
                    break;
                }
                moveCandidates.add(next);
                break;
            }
            moveCandidates.add(next);
            next = LocationFactory.build(next, offset, 0);
        }
    }

    private void getRankCandidates(
            List<Location> moveCandidates,
            Map<Location, Square> squareMap,
            Location current,
            int offset) {

        Location next = LocationFactory.build(current, 0, offset);
        while (next != null && squareMap.containsKey(next)) {
            Square square = squareMap.get(next);
            if (square.isOccupied()) {
                if (square.getCurrentPiece().getPieceColor().equals(this.getPieceColor())) {
                    break;
                }
                moveCandidates.add(next);
                break;
            }
            moveCandidates.add(next);
            next = LocationFactory.build(next, 0, offset);
        }
    }
}
