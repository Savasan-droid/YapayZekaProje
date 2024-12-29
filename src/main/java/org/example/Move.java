package org.example;

public class Move {
    public int fromRow, fromCol, toRow, toCol;  // değişkenleri public yap

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
}
