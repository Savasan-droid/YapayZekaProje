package org.example;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {
    private final int BOARD_SIZE = 7;
    private final int CELL_SIZE = 60;
    private final Game game;
    private final AI ai;
    private JButton[][] boardButtons;
    private Point selectedPiece = null;
    private JLabel statusLabel;
    private final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public GameGUI() {
        boardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        statusLabel = new JLabel("Oyun başladı! İnsan oyuncu: ○, AI: ▲");
        game = new Game();
        ai = new AI();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Strategic Board Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardButtons[i][j] = createBoardButton(i, j);
                boardPanel.add(boardButtons[i][j]);
            }
        }

        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(BACKGROUND_COLOR);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(BOARD_SIZE * CELL_SIZE + 100, BOARD_SIZE * CELL_SIZE + 100);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        updateBoard();
    }

    private JButton createBoardButton(int row, int col) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Kare çizgileri
                g2d.setColor(Color.WHITE);
                g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);

                // Taşları çiz
                if (getText().equals("○")) {
                    g2d.setColor(Color.BLUE);
                    int padding = getWidth()/4;
                    g2d.fillOval(padding, padding, getWidth()-2*padding, getHeight()-2*padding);
                } else if (getText().equals("▲")) {
                    g2d.setColor(Color.RED);
                    int[] xPoints = {getWidth()/4, getWidth()/2, 3*getWidth()/4};
                    int[] yPoints = {3*getHeight()/4, getHeight()/4, 3*getHeight()/4};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            }
        };
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setFont(new Font("Dialog", Font.BOLD, 24));
        button.setBackground(BACKGROUND_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> handleButtonClick(row, col));
        return button;
    }

    private void handleButtonClick(int row, int col) {
        if (game.getCurrentPlayer() != Board.PlayerType.PLAYER2) {
            return;
        }

        if (selectedPiece == null) {
            if (game.getBoard().getPiece(row, col).getPlayer() == Board.PlayerType.PLAYER2) {
                selectedPiece = new Point(row, col);
                boardButtons[row][col].setBackground(Color.YELLOW);
            }
        } else {
            if (game.makeMove(selectedPiece.x, selectedPiece.y, row, col)) {
                updateBoard();
                checkGameState();

                if (game.getCurrentPlayer() == Board.PlayerType.PLAYER1) {
                    makeAIMove();
                }
            }

            boardButtons[selectedPiece.x][selectedPiece.y].setBackground(BACKGROUND_COLOR);
            selectedPiece = null;
        }
    }

    private void makeAIMove() {
        if (game.getCurrentPlayer() != Board.PlayerType.PLAYER1) {
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    statusLabel.setText("AI düşünüyor...");
                    Thread.sleep(500);

                    Move bestMove = ai.findBestMove(game.getBoard(), game.getRemainingMovesInTurn());
                    if (bestMove != null) {
                        highlightAIMove(bestMove);
                        Thread.sleep(500);
                        game.makeMove(bestMove.fromRow, bestMove.fromCol, bestMove.toRow, bestMove.toCol);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            }

            @Override
            protected void done() {
                updateBoard();
                if (game.getCurrentPlayer() == Board.PlayerType.PLAYER1 &&
                        game.checkGameState() == Game.GameState.ONGOING) {
                    makeAIMove();
                } else {
                    statusLabel.setText("Sizin sıranız!");
                }
                checkGameState();
            }
        };
        worker.execute();
    }

    private void highlightAIMove(Move move) {
        boardButtons[move.fromRow][move.fromCol].setBackground(Color.YELLOW);
        boardButtons[move.toRow][move.toCol].setBackground(Color.GREEN);
        boardButtons[move.fromRow][move.fromCol].repaint();
        boardButtons[move.toRow][move.toCol].repaint();
    }

    private void updateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Board.PlayerType player = game.getBoard().getPiece(i, j).getPlayer();
                JButton button = boardButtons[i][j];

                button.setBackground(BACKGROUND_COLOR);

                switch (player) {
                    case PLAYER1:
                        button.setText("▲");
                        button.setForeground(Color.RED);
                        break;
                    case PLAYER2:
                        button.setText("○");
                        button.setForeground(Color.BLUE);
                        break;
                    case EMPTY:
                        button.setText("");
                        break;
                }
            }
        }
    }

    private void checkGameState() {
        Game.GameState state = game.checkGameState();
        if (state != Game.GameState.ONGOING) {
            String message = switch (state) {
                case PLAYER1_WINS -> "AI kazandı!";
                case PLAYER2_WINS -> "Tebrikler, kazandınız!";
                case DRAW -> "Oyun berabere bitti!";
                default -> "";
            };

            JOptionPane.showMessageDialog(this, message);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI();
            gui.setVisible(true);
            if (gui.game.getCurrentPlayer() == Board.PlayerType.PLAYER1) {
                gui.makeAIMove();
            }
        });
    }
}