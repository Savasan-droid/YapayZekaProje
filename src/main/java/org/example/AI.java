package org.example;

import java.util.ArrayList;
import java.util.List;

public class AI {
    private static final int MAX_DEPTH = 4;
    private Board.PlayerType aiPlayer;
    private Board.PlayerType humanPlayer;

    public AI() {
        this.aiPlayer = Board.PlayerType.PLAYER1;
        this.humanPlayer = Board.PlayerType.PLAYER2;
    }


    public Move findBestMove(Board board, int remainingMoves) {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;

        List<Move> possibleMoves = generateMoves(board, aiPlayer, remainingMoves);

        for (Move move : possibleMoves) {
            Board tempBoard = cloneBoard(board);
            tempBoard.makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);

            int moveValue = minimax(tempBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Minimax algoritması
    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0) {
            int endGameScore = 0;
            int aiPieces = countPieces(board, aiPlayer);
            int humanPieces = countPieces(board, humanPlayer);

            if (aiPieces <= 2 || humanPieces <= 2) {

                endGameScore = evaluateEndGameCaptures(board) * 3000;
            }

            return evaluateBoard(board) + endGameScore;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> moves = generateMoves(board, aiPlayer, 2);


            moves.sort((m1, m2) -> {
                Board b1 = cloneBoard(board);
                Board b2 = cloneBoard(board);
                b1.makeMove(m1.fromRow, m1.fromCol, m1.toRow, m1.toCol);
                b2.makeMove(m2.fromRow, m2.fromCol, m2.toRow, m2.toCol);
                return evaluateEndGameCaptures(b2) - evaluateEndGameCaptures(b1);
            });

            for (Move move : moves) {
                Board tempBoard = cloneBoard(board);
                tempBoard.makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
                int eval = minimax(tempBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {

            int minEval = Integer.MAX_VALUE;
            List<Move> moves = generateMoves(board, humanPlayer, 2);
            for (Move move : moves) {
                Board tempBoard = cloneBoard(board);
                tempBoard.makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
                int eval = minimax(tempBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluateEndGameCaptures(Board board) {
        int score = 0;

        for (int r = 0; r < 7; r++) {
            for (int c = 1; c < 6; c++) {
                if (board.getPiece(r, c).getPlayer() == humanPlayer) {
                    // İki hamle sonrası yakalama potansiyeli
                    if (c > 0 && c < 5 && board.getPiece(r, c-1).getPlayer() == aiPlayer) {
                        if (board.getPiece(r, c+1).getPlayer() == Board.PlayerType.EMPTY &&
                                board.getPiece(r, c+2).getPlayer() == Board.PlayerType.EMPTY) {
                            score += 1000;
                        }
                    }
                }
            }
        }


        for (int c = 0; c < 7; c++) {
            for (int r = 1; r < 6; r++) {
                if (board.getPiece(r, c).getPlayer() == humanPlayer) {
                    if (r > 0 && r < 5 && board.getPiece(r-1, c).getPlayer() == aiPlayer) {
                        if (board.getPiece(r+1, c).getPlayer() == Board.PlayerType.EMPTY &&
                                board.getPiece(r+2, c).getPlayer() == Board.PlayerType.EMPTY) {
                            score += 1000;
                        }
                    }
                }
            }
        }
        return score;
    }


    private int evaluateBoard(Board board) {
        int score = 0;
        int aiPieces = countPieces(board, aiPlayer);
        int humanPieces = countPieces(board, humanPlayer);


        score += (aiPieces - humanPieces) * 1000;


        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (board.getPiece(r, c).getPlayer() == aiPlayer) {

                    if (r == 0 || r == 6 || c == 0 || c == 6) {
                        score -= 500;
                    }

                    if (isUnderThreat(board, r, c)) {
                        score -= 800;
                    }
                }

                if (canCaptureInNextMove(board, r, c)) {
                    score += 600;
                }
            }
        }

        return score;
    }

    private boolean isUnderThreat(Board board, int row, int col) {

        if (col > 0 && col < 6 &&
                board.getPiece(row, col-1).getPlayer() == humanPlayer &&
                board.getPiece(row, col+1).getPlayer() == humanPlayer) {
            return true;
        }

        if (row > 0 && row < 6 &&
                board.getPiece(row-1, col).getPlayer() == humanPlayer &&
                board.getPiece(row+1, col).getPlayer() == humanPlayer) {
            return true;
        }
        return false;
    }

    private boolean canCaptureInNextMove(Board board, int row, int col) {
        if (board.getPiece(row, col).getPlayer() != aiPlayer) return false;

        // Yatay ve dikey yönlerde yakalama fırsatı kontrolü
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol) &&
                    board.getPiece(newRow, newCol).getPlayer() == humanPlayer) {
                return true;
            }
        }
        return false;
    }


    private int evaluatePosition(Board board) {
        int score = 0;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (board.getPiece(r, c).getPlayer() == aiPlayer) {
                    // Merkeze yakınlık bonusu
                    score += 3 - (Math.abs(3 - r) + Math.abs(3 - c)) / 2;

                    // Duvar kenarı cezası
                    if (r == 0 || r == 6 || c == 0 || c == 6) {
                        score -= 2;
                    }
                }
            }
        }
        return score * 50;
    }


    private int evaluateCapturePotential(Board board) {
        int score = 0;


        for (int r = 0; r < 7; r++) {
            for (int c = 1; c < 6; c++) {
                if (board.getPiece(r, c).getPlayer() == humanPlayer) {
                    // Tek taş yakalama
                    if (canCapturePiece(board, r, c)) {
                        score += 100;
                    }
                    // Grup yakalama
                    if (canCaptureGroup(board, r, c)) {
                        score += 200;
                    }
                }
            }
        }


        for (int c = 0; c < 7; c++) {
            for (int r = 1; r < 6; r++) {
                if (board.getPiece(r, c).getPlayer() == humanPlayer) {
                    if (canCapturePiece(board, r, c)) {
                        score += 100;
                    }
                    if (canCaptureGroup(board, r, c)) {
                        score += 200;
                    }
                }
            }
        }

        return score;
    }


    private boolean canCapturePiece(Board board, int row, int col) {

        if (col > 0 && col < 6) {
            if (board.getPiece(row, col-1).getPlayer() == aiPlayer &&
                    board.getPiece(row, col+1).getPlayer() == aiPlayer) {
                return true;
            }
        }

        if (row > 0 && row < 6) {
            if (board.getPiece(row-1, col).getPlayer() == aiPlayer &&
                    board.getPiece(row+1, col).getPlayer() == aiPlayer) {
                return true;
            }
        }
        return false;
    }

    // Grup yakalama potansiyel
    private boolean canCaptureGroup(Board board, int row, int col) {
        Board.PlayerType piece = board.getPiece(row, col).getPlayer();
        if (piece != humanPlayer) return false;

        // Yatay grup kontrolü
        if (col < 5 && board.getPiece(row, col+1).getPlayer() == piece) {
            if ((col > 0 && board.getPiece(row, col-1).getPlayer() == aiPlayer) ||
                    (col < 4 && board.getPiece(row, col+2).getPlayer() == aiPlayer)) {
                return true;
            }
        }

        // Dikey grup kontrolü
        if (row < 5 && board.getPiece(row+1, col).getPlayer() == piece) {
            if ((row > 0 && board.getPiece(row-1, col).getPlayer() == aiPlayer) ||
                    (row < 4 && board.getPiece(row+2, col).getPlayer() == aiPlayer)) {
                return true;
            }
        }

        return false;
    }


    private int countPieces(Board board, Board.PlayerType player) {
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

    private List<Move> generateMoves(Board board, Board.PlayerType player, int remainingMoves) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (board.getPiece(r, c).getPlayer() == player) {

                    int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
                    for (int[] dir : directions) {
                        int newRow = r + dir[0];
                        int newCol = c + dir[1];
                        if (isValidPosition(newRow, newCol) &&
                                board.getPiece(newRow, newCol).getPlayer() == Board.PlayerType.EMPTY) {
                            moves.add(new Move(r, c, newRow, newCol));
                        }
                    }
                }
            }
        }
        return moves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 7 && col >= 0 && col < 7;
    }

    private Board cloneBoard(Board original) {
        Board clone = new Board();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Board.Piece originalPiece = original.getPiece(i, j);
                clone.setPiece(i, j, new Board.Piece(
                        originalPiece.getPlayer(),
                        originalPiece.getRow(),
                        originalPiece.getCol()
                ));
            }
        }
        return clone;
    }
}