package com.chess.piece;

import com.chess.board.Board;
import com.chess.common.Location;
import com.chess.common.LocationFactory;
import com.chess.squares.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Knight extends AbstractPiece implements Movable {

    public Knight(PieceColor pieceColor) {
        super(pieceColor);
        this.name = "Knight";
    }

    @Override
    public List<Location> getValidMoves(Board board) {
        List<Location> moveCandidates = new ArrayList<>();
        Map<Location, Square> squareMap = board.getLocationSquareMap();
        Location current = this.getCurrentSquare().getLocation();
        int[][] offsets = {
                {-2, 1}, {-1, 2}, {1, 2}, {2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };
        for (int[] offset : offsets) {
            Location candidate = LocationFactory.build(current, offset[0], offset[1]);
            if (candidate == null || !squareMap.containsKey(candidate)) {
                continue;
            }
            Square square = squareMap.get(candidate);
            if (!square.isOccupied() ||
                    !square.getCurrentPiece().getPieceColor().equals(this.getPieceColor())) {
                moveCandidates.add(candidate);
            }
        }
        return moveCandidates;
    }

    @Override
    public List<Location> getValidMoves(Board board, Square square) {
        return getValidMoves(board);
    }
}
