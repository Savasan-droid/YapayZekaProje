package org.example;

import java.util.Scanner;

public class Main {
    private static Game game;
    private static AI ai;
    private static Scanner scanner;

    public static void main(String[] args) {
        game = new Game();
        ai = new AI();
        scanner = new Scanner(System.in);

        System.out.println("Oyun başlıyor!");
        System.out.println("AI: ▲ (Player 1)");
        System.out.println("İnsan: ○ (Player 2)");


        while (true) {

            game.getBoard().printBoard();


            Game.GameState state = game.checkGameState();
            if (state != Game.GameState.ONGOING) {
                announceWinner(state);
                break;
            }


            if (game.getCurrentPlayer() == Board.PlayerType.PLAYER1) {
                makeAIMove();
            } else {
                makeHumanMove();
            }
        }

        scanner.close();
    }

    private static void makeAIMove() {
        System.out.println("\nAI düşünüyor...");


        Move bestMove = ai.findBestMove(game.getBoard(), game.getRemainingMovesInTurn());


        game.makeMove(bestMove.fromRow, bestMove.fromCol, bestMove.toRow, bestMove.toCol);

        System.out.printf("AI hamle yaptı: %d,%d -> %d,%d\n",
                bestMove.fromRow, bestMove.fromCol, bestMove.toRow, bestMove.toCol);
    }

    private static void makeHumanMove() {
        while (true) {
            try {
                System.out.println("\nSıra sizde! Kalan hamle sayısı: " + game.getRemainingMovesInTurn());
                System.out.print("Başlangıç konumu (satır,sütun): ");
                String[] start = scanner.nextLine().split(",");
                int fromRow = Integer.parseInt(start[0].trim());
                int fromCol = Integer.parseInt(start[1].trim());

                System.out.print("Hedef konumu (satır,sütun): ");
                String[] end = scanner.nextLine().split(",");
                int toRow = Integer.parseInt(end[0].trim());
                int toCol = Integer.parseInt(end[1].trim());


                if (game.makeMove(fromRow, fromCol, toRow, toCol)) {
                    break;
                } else {
                    System.out.println("Geçersiz hamle! Tekrar deneyin.");
                }
            } catch (Exception e) {
                System.out.println("Hatalı giriş! Lütfen 'satır,sütun' formatında giriş yapın.");
                scanner.nextLine(); // Buffer'ı temizle
            }
        }
    }

    private static void announceWinner(Game.GameState state) {
        System.out.println("\nOyun bitti!");
        switch (state) {
            case PLAYER1_WINS:
                System.out.println("AI kazandı!");
                break;
            case PLAYER2_WINS:
                System.out.println("Tebrikler, siz kazandınız!");
                break;
            case DRAW:
                System.out.println("Oyun berabere bitti!");
                break;
        }

        game.getBoard().printBoard();
    }
}