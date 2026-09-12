package com.chess.common;

public class LocationFactory {
    private static final File[] files = File.values();

    // Returns null when the offset would fall off the board (instead of
    // throwing ArrayIndexOutOfBoundsException), so callers can just check
    // squareMap.containsKey(...) the same way they already do.
    public static Location build(Location current, Integer fileOffset, Integer rankOffset) {
        int newFileIndex = current.getFile().ordinal() + fileOffset;
        if (newFileIndex < 0 || newFileIndex >= files.length) {
            return null;
        }
        int newRank = current.getRank() + rankOffset;
        return new Location(files[newFileIndex], newRank);
    }
}
