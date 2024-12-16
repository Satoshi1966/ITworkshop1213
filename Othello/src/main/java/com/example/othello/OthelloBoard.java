package com.example.othello;

public class OthelloBoard {
    private char[][] board;
    private char currentPlayer;

    public OthelloBoard() {
        board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = '-';
            }
        }
        board[3][3] = board[4][4] = 'W';
        board[3][4] = board[4][3] = 'B';
        currentPlayer = 'B';
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void makeMove(int row, int col) {
        if (board[row][col] == '-') {
            board[row][col] = currentPlayer;
            flipDiscs(row, col);
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
        }
    }

    private void flipDiscs(int row, int col) {
        // Implement the logic to flip the discs according to Othello rules
    }
}
