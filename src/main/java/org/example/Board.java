package org.example;

public class Board {
    private static final int BOARD_SIZE = 7;
    private Piece[][] board;

    public enum PlayerType {
        PLAYER1,
        PLAYER2,
        EMPTY
    }

    public static class Piece {
        private PlayerType player;
        private int row;
        private int col;

        public Piece(PlayerType player, int row, int col) {
            this.player = player;
            this.row = row;
            this.col = col;
        }

        public PlayerType getPlayer() { return player; }
        public int getRow() { return row; }
        public int getCol() { return col; }
        public void setPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }


    public Board() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }


    private void initializeBoard() {
        // Tüm hücreleri boş olarak işaretle
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Piece(PlayerType.EMPTY, i, j);
            }
        }


        board[0][0] = new Piece(PlayerType.PLAYER1, 0, 0);
        board[2][0] = new Piece(PlayerType.PLAYER1, 2, 0);
        board[4][6] = new Piece(PlayerType.PLAYER1, 4, 6);
        board[6][6] = new Piece(PlayerType.PLAYER1, 6, 6);


        board[0][6] = new Piece(PlayerType.PLAYER2, 0, 6);
        board[2][6] = new Piece(PlayerType.PLAYER2, 2, 6);
        board[4][0] = new Piece(PlayerType.PLAYER2, 4, 0);
        board[6][0] = new Piece(PlayerType.PLAYER2, 6, 0);
    }


    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, PlayerType currentPlayer) {

        if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) {
            return false;
        }


        if (board[fromRow][fromCol].getPlayer() != currentPlayer) {
            return false;
        }


        if (board[toRow][toCol].getPlayer() != PlayerType.EMPTY) {
            return false;
        }


        return (fromRow == toRow && Math.abs(fromCol - toCol) == 1) ||
                (fromCol == toCol && Math.abs(fromRow - toRow) == 1);
    }

    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board[fromRow][fromCol];
        board[toRow][toCol] = piece;
        piece.setPosition(toRow, toCol);
        board[fromRow][fromCol] = new Piece(PlayerType.EMPTY, fromRow, fromCol);


        checkAllCaptures();
    }


    private void checkAllCaptures() {
        boolean captureOccurred;
        do {
            captureOccurred = false;

            for (PlayerType player : PlayerType.values()) {
                if (player != PlayerType.EMPTY) {
                    if (checkCapturesForPlayer(player)) {
                        captureOccurred = true;
                    }
                }
            }
        } while (captureOccurred); // Yakalama oldukça kontrol etmeye devam et
    }

    private boolean checkCapturesForPlayer(PlayerType player) {
        boolean captured = false;


        captured |= checkWallCaptures(player);


        captured |= checkPieceBetweenCaptures(player);

        return captured;
    }

    private boolean checkWallCaptures(PlayerType movingPlayer) {
        boolean captured = false;


        for (int r = 0; r < BOARD_SIZE; r++) {
            if (board[r][0].getPlayer() != PlayerType.EMPTY &&
                    board[r][0].getPlayer() != movingPlayer) {

                int groupEnd = 0;
                while (groupEnd < BOARD_SIZE - 1 &&
                        board[r][groupEnd + 1].getPlayer() == board[r][0].getPlayer()) {
                    groupEnd++;
                }

                if (groupEnd < BOARD_SIZE - 1 &&
                        board[r][groupEnd + 1].getPlayer() == movingPlayer) {
                    // Gruptaki tüm taşları kaldır
                    for (int i = 0; i <= groupEnd; i++) {
                        board[r][i] = new Piece(PlayerType.EMPTY, r, i);
                        captured = true;
                    }
                }
            }
        }


        for (int r = 0; r < BOARD_SIZE; r++) {
            if (board[r][BOARD_SIZE-1].getPlayer() != PlayerType.EMPTY &&
                    board[r][BOARD_SIZE-1].getPlayer() != movingPlayer) {

                int groupStart = BOARD_SIZE - 1;
                while (groupStart > 0 &&
                        board[r][groupStart - 1].getPlayer() == board[r][BOARD_SIZE-1].getPlayer()) {
                    groupStart--;
                }

                if (groupStart > 0 &&
                        board[r][groupStart - 1].getPlayer() == movingPlayer) {

                    for (int i = groupStart; i < BOARD_SIZE; i++) {
                        board[r][i] = new Piece(PlayerType.EMPTY, r, i);
                        captured = true;
                    }
                }
            }
        }


        for (int c = 0; c < BOARD_SIZE; c++) {
            if (board[0][c].getPlayer() != PlayerType.EMPTY &&
                    board[0][c].getPlayer() != movingPlayer) {
                int groupEnd = 0;
                while (groupEnd < BOARD_SIZE - 1 &&
                        board[groupEnd + 1][c].getPlayer() == board[0][c].getPlayer()) {
                    groupEnd++;
                }
                if (groupEnd < BOARD_SIZE - 1 &&
                        board[groupEnd + 1][c].getPlayer() == movingPlayer) {
                    for (int i = 0; i <= groupEnd; i++) {
                        board[i][c] = new Piece(PlayerType.EMPTY, i, c);
                        captured = true;
                    }
                }
            }
        }


        for (int c = 0; c < BOARD_SIZE; c++) {
            if (board[BOARD_SIZE-1][c].getPlayer() != PlayerType.EMPTY &&
                    board[BOARD_SIZE-1][c].getPlayer() != movingPlayer) {
                int groupStart = BOARD_SIZE - 1;
                while (groupStart > 0 &&
                        board[groupStart - 1][c].getPlayer() == board[BOARD_SIZE-1][c].getPlayer()) {
                    groupStart--;
                }
                if (groupStart > 0 &&
                        board[groupStart - 1][c].getPlayer() == movingPlayer) {
                    for (int i = groupStart; i < BOARD_SIZE; i++) {
                        board[i][c] = new Piece(PlayerType.EMPTY, i, c);
                        captured = true;
                    }
                }
            }
        }

        return captured;
    }


    private boolean checkPieceBetweenCaptures(PlayerType movingPlayer) {
        boolean captured = false;


        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (board[r][c].getPlayer() != PlayerType.EMPTY &&
                        board[r][c].getPlayer() != movingPlayer) {


                    int groupEndCol = c;
                    while (groupEndCol < BOARD_SIZE - 1 &&
                            board[r][groupEndCol + 1].getPlayer() == board[r][c].getPlayer()) {
                        groupEndCol++;
                    }


                    if (c > 0 && groupEndCol < BOARD_SIZE - 1) {

                        if (board[r][c-1].getPlayer() == movingPlayer &&
                                board[r][groupEndCol+1].getPlayer() == movingPlayer) {

                            for (int i = c; i <= groupEndCol; i++) {
                                board[r][i] = new Piece(PlayerType.EMPTY, r, i);
                                captured = true;
                            }
                        }
                    }
                    c = groupEndCol;
                }
            }
        }


        for (int c = 0; c < BOARD_SIZE; c++) {
            for (int r = 0; r < BOARD_SIZE - 1; r++) {
                if (board[r][c].getPlayer() != PlayerType.EMPTY &&
                        board[r][c].getPlayer() != movingPlayer) {


                    int groupEndRow = r;
                    while (groupEndRow < BOARD_SIZE - 1 &&
                            board[groupEndRow + 1][c].getPlayer() == board[r][c].getPlayer()) {
                        groupEndRow++;
                    }


                    if (r > 0 && groupEndRow < BOARD_SIZE - 1) {

                        if (board[r-1][c].getPlayer() == movingPlayer &&
                                board[groupEndRow+1][c].getPlayer() == movingPlayer) {

                            for (int i = r; i <= groupEndRow; i++) {
                                board[i][c] = new Piece(PlayerType.EMPTY, i, c);
                                captured = true;
                            }
                        }
                    }
                    r = groupEndRow;
                }
            }
        }

        return captured;
    }


    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }


    public void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                switch (board[i][j].getPlayer()) {
                    case PLAYER1:
                        System.out.print("▲ ");
                        break;
                    case PLAYER2:
                        System.out.print("○ ");
                        break;
                    case EMPTY:
                        System.out.print(". ");
                        break;
                }
            }
            System.out.println();
        }
    }


    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        if (isInBounds(row, col)) {
            board[row][col] = piece;
        }
    }

    public boolean isPlayersPiece(int row, int col, PlayerType player) {
        return isInBounds(row, col) && board[row][col].getPlayer() == player;
    }


    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) {
            return false;
        }

        Piece piece = board[fromRow][fromCol];
        board[toRow][toCol] = piece;
        piece.setPosition(toRow, toCol);
        board[fromRow][fromCol] = new Piece(PlayerType.EMPTY, fromRow, fromCol);
        return true;
    }


    public void updatePosition(int row, int col, PlayerType player) {
        if (isInBounds(row, col)) {
            board[row][col] = new Piece(player, row, col);
        }
    }
}

