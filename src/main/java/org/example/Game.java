package org.example;

public class Game {
    private Board board;
    private Board.PlayerType currentPlayer;
    private int totalMoves;
    private static final int MAX_MOVES = 50;
    private int remainingMovesInTurn; // Her turda yapılabilecek hamle sayısı

    public Game() {
        board = new Board();
        currentPlayer = Board.PlayerType.PLAYER1; // AI starts
        totalMoves = 0;
        remainingMovesInTurn = 2; // Her oyuncu 2 hamle yapabilir
    }


    public GameState checkGameState() {
        int player1Pieces = countPieces(Board.PlayerType.PLAYER1);
        int player2Pieces = countPieces(Board.PlayerType.PLAYER2);


        if (totalMoves >= MAX_MOVES) {
            if (player1Pieces == player2Pieces) return GameState.DRAW;
            return player1Pieces > player2Pieces ? GameState.PLAYER1_WINS : GameState.PLAYER2_WINS;
        }

        if (player1Pieces == 0 && player2Pieces > 0) return GameState.PLAYER2_WINS;
        if (player2Pieces == 0 && player1Pieces > 0) return GameState.PLAYER1_WINS;
        if (player1Pieces == 1 && player2Pieces == 1) return GameState.DRAW;
        if (player1Pieces == 0 && player2Pieces == 0) return GameState.DRAW;

        return GameState.ONGOING;
    }


    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Oyun bittiyse hamle yapılamaz
        if (checkGameState() != GameState.ONGOING) {
            return false;
        }


        if (!board.isValidMove(fromRow, fromCol, toRow, toCol, currentPlayer)) {
            return false;
        }


        board.makeMove(fromRow, fromCol, toRow, toCol);
        remainingMovesInTurn--;


        if (remainingMovesInTurn == 0) {
            switchPlayer();
            totalMoves++;
        }

        return true;
    }


    private void switchPlayer() {
        currentPlayer = (currentPlayer == Board.PlayerType.PLAYER1) ?
                Board.PlayerType.PLAYER2 : Board.PlayerType.PLAYER1;


        int pieces = countPieces(currentPlayer);
        remainingMovesInTurn = (pieces > 1) ? 2 : 1;
    }


    private int countPieces(Board.PlayerType player) {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (board.getPiece(i, j).getPlayer() == player) {
                    count++;
                }
            }
        }
        return count;
    }


    public Board getBoard() {
        return board;
    }

    public Board.PlayerType getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRemainingMovesInTurn() {
        return remainingMovesInTurn;
    }

    public int getTotalMoves() {
        return totalMoves;
    }


    public enum GameState {
        ONGOING,
        PLAYER1_WINS,
        PLAYER2_WINS,
        DRAW
    }
}
