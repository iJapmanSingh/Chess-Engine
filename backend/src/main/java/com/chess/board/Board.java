package com.chess.board;

import com.chess.common.File;
import com.chess.common.Location;
import com.chess.piece.AbstractPiece;
import com.chess.piece.PieceColor;
import com.chess.piece.PieceFactory;
import com.chess.squares.Square;
import com.chess.squares.SquareColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private static final Integer BOARD_LENGTH = 8 ;
    private final Map<Location , Square> locationSquareMap ;

    Square[][] boardSquares = new Square[BOARD_LENGTH][BOARD_LENGTH ];

    private final List<AbstractPiece> lightPieces = new ArrayList<>();
    private final List<AbstractPiece> darkPieces = new ArrayList<>();

    public Board(){
        locationSquareMap = new HashMap<>();
        Map<Location , AbstractPiece> pieces = PieceFactory.getPieces();
        for (int i = 0; i < boardSquares.length ; i++){
            int column = 0 ;
            SquareColor currentColor = (i % 2 == 0) ? SquareColor.LIGHT : SquareColor.DARK;
            for(File file : File.values()){
                Square newSquare = new Square(currentColor , new Location(file , BOARD_LENGTH - i));
                if (pieces.containsKey(newSquare.getLocation())) {
                    AbstractPiece piece = pieces.get(newSquare.getLocation());
                    newSquare.setCurrentPiece(piece);
                    newSquare.setOccupied(true);
                    piece.setCurrentSquare(newSquare);
                    if(piece.getPieceColor().equals(PieceColor.DARK)){
                        darkPieces.add(piece);
                    }else{
                        lightPieces.add(piece);
                    }
                }
                locationSquareMap.put(newSquare.getLocation() , newSquare);
                boardSquares[i][column] = newSquare ;
                currentColor = (currentColor == SquareColor.DARK) ? SquareColor.LIGHT : SquareColor.DARK ;
                column++ ;
            }
        }
    }

    public Map<Location, Square> getLocationSquareMap() {
        return locationSquareMap;
    }


    public List<AbstractPiece> getLightPieces(){
        return lightPieces;
    }
    public List<AbstractPiece> getDarkPieces(){
        return darkPieces;
    }

    public Square getSquare(Location location) {
        return locationSquareMap.get(location);
    }

    public List<AbstractPiece> getPieces(PieceColor color) {
        return color == PieceColor.LIGHT ? lightPieces : darkPieces;
    }

    public void addPiece(AbstractPiece piece) {
        getPieces(piece.getPieceColor()).add(piece);
    }

    public void removePiece(AbstractPiece piece) {
        getPieces(piece.getPieceColor()).remove(piece);
    }

    public void printBoard(){
        for(int i =0 ; i < boardSquares.length ; i++){
            System.out.print(BOARD_LENGTH - i + " ");
            for(int j =0 ; j < boardSquares[i].length ; j++){
                if(boardSquares[i][j].isOccupied()){
                    AbstractPiece piece = boardSquares[i][j].getCurrentPiece();
                    // Knight -> N (K is the King); LIGHT = UPPERCASE, DARK = lowercase
                    char symbol = piece.getName().equals("Knight") ? 'N' : piece.getName().charAt(0);
                    System.out.print((piece.getPieceColor() == PieceColor.LIGHT
                            ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol)) + " ");
                }else{
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
        System.out.print("  ");
        for(File file : File.values()){
            System.out.print(file.name() + " ");
        }
        System.out.println();
    }
}
