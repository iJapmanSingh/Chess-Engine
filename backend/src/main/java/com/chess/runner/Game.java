package com.chess.runner;

import com.chess.common.File;
import com.chess.common.Location;
import com.chess.game.ChessGame;
import com.chess.game.GameStatus;
import com.chess.game.IllegalMoveException;

import java.util.Scanner;

/** Console version. The rules live in ChessGame; this only reads input and prints. */
public class Game {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.getBoard().printBoard();

        Scanner scanner = new Scanner(System.in);
        while (game.getStatus() == GameStatus.IN_PROGRESS) {
            System.out.print(game.getTurn() + " to move (e.g. E2->E4): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String[] fromTo = scanner.nextLine().trim().split("->");
            try {
                Location from = parse(fromTo[0].trim());
                Location to = parse(fromTo[1].trim());
                game.move(from, to);
            } catch (IllegalMoveException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (Exception e) {
                System.out.println("Couldn't understand that move. Use the format E2->E4.");
                continue;
            }
            game.getBoard().printBoard();
            if (game.isInCheck() && game.getStatus() == GameStatus.IN_PROGRESS) {
                System.out.println("Check!");
            }
        }

        if (game.getStatus() == GameStatus.CHECKMATE) {
            System.out.println("Checkmate! " + game.getWinner() + " wins.");
        } else if (game.getStatus() == GameStatus.STALEMATE) {
            System.out.println("Stalemate - it's a draw.");
        }
    }

    private static Location parse(String square) {
        return new Location(File.valueOf(square.substring(0, 1).toUpperCase()),
                Integer.parseInt(square.substring(1, 2)));
    }
}
