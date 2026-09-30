package com.chess.runner;

import com.chess.board.Board;
import com.chess.common.File;
import com.chess.common.Location;
import com.chess.piece.*;
import com.chess.squares.Square;

import java.util.List;
import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Board board = new Board();
        board.printBoard();

        Scanner scanner = new Scanner(System.in);
        PieceColor currentTurn = PieceColor.LIGHT;

        //true -> game is not finished
        while (true) {
            System.out.print(currentTurn + " to move (e.g. E2->E4): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();

            Square fromSq;
            Square toSq;
            try {
                String[] fromTo = line.split("->");

                File fromFile = File.valueOf(String.valueOf(Character.toUpperCase(fromTo[0].charAt(0))));
                int fromRank = Integer.parseInt(String.valueOf(fromTo[0].charAt(1)));

                File toFile = File.valueOf(String.valueOf(Character.toUpperCase(fromTo[1].charAt(0))));
                int toRank = Integer.parseInt(String.valueOf(fromTo[1].charAt(1)));

                fromSq = board.getLocationSquareMap().get(new Location(fromFile, fromRank));
                toSq = board.getLocationSquareMap().get(new Location(toFile, toRank));
            } catch (Exception e) {
                System.out.println("Couldn't understand that move. Use the format E2->E4.");
                continue;
            }

            if (fromSq == null || toSq == null) {
                System.out.println("That square is off the board.");
                continue;
            }

            AbstractPiece piece = fromSq.getCurrentPiece();
            if (piece == null) {
                System.out.println("There's no piece on that square.");
                continue;
            }
            if (!piece.getPieceColor().equals(currentTurn)) {
                System.out.println("It's " + currentTurn + "'s turn.");
                continue;
            }

            List<Location> validMoves = piece.getValidMoves(board);
            if (!validMoves.contains(toSq.getLocation())) {
                System.out.println(piece.getName() + " can't move there.");
                continue;
            }

            piece.makeMoves(toSq);

            currentTurn = (currentTurn == PieceColor.LIGHT) ? PieceColor.DARK : PieceColor.LIGHT;

            board.printBoard();
        }
    }

    public static void printPiece(Movable piece) {
        piece.getValidMoves(null);
    }
}
