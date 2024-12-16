package com.example.othello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/othello")
public class OthelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private char[][] board;
    private char currentPlayer;
    private static final char EMPTY = '-';
    private static final char PLAYER = 'B';
    private static final char AI = 'W';
    private boolean aiMovePending = false;
    private boolean gameFinished = false;
    private int playerScore = 0;
    private int aiScore = 0;

    @Override
    public void init() throws ServletException {
        board = initializeBoard();
        currentPlayer = PLAYER;
        aiMovePending = false;
        gameFinished = false;
        playerScore = 0;
        aiScore = 0;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("reset".equals(action)) {
            init();
        } else if ("giveup".equals(action)) {
            gameFinished = true;
            playerScore = countDiscs(PLAYER);
            aiScore = countDiscs(AI);
            aiScore += (64 - playerScore - aiScore); // 残りの空白をAIの得点に加算
        } else if ("skip".equals(action) && currentPlayer == PLAYER) {
            aiMovePending = true;
        }

        if (!gameFinished) {
            if (request.getParameter("move") != null) {
                String[] move = request.getParameter("move").split(",");
                int row = Integer.parseInt(move[0]);
                int col = Integer.parseInt(move[1]);
                if (isValidMove(row, col, PLAYER)) {
                    makeMove(row, col, PLAYER);
                    aiMovePending = true;
                    if (!hasValidMove(AI) && !hasValidMove(PLAYER)) {
                        gameFinished = true;
                    }
                }
            } else if ("true".equals(request.getParameter("aiMove")) && aiMovePending) {
                makeAIMove();
                aiMovePending = false;
                if (!hasValidMove(PLAYER) && !hasValidMove(AI)) {
                    gameFinished = true;
                }
            }
        }

        if (gameFinished || (!hasValidMove(PLAYER) && !hasValidMove(AI))) {
            playerScore = countDiscs(PLAYER);
            aiScore = countDiscs(AI);
        }

        request.setAttribute("board", board);
        request.setAttribute("aiMovePending", aiMovePending);
        request.setAttribute("gameFinished", gameFinished);
        request.setAttribute("playerScore", playerScore);
        request.setAttribute("aiScore", aiScore);
        
        request.getRequestDispatcher("/othello.jsp").forward(request, response);
    }

    private char[][] initializeBoard() {
        char[][] board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EMPTY;
            }
        }
        board[3][3] = AI;
        board[3][4] = PLAYER;
        board[4][3] = PLAYER;
        board[4][4] = AI;
        return board;
    }

    private void makeMove(int row, int col, char player) {
        if (isValidMove(row, col, player)) {
            board[row][col] = player;
            flipDiscs(row, col, player);
            currentPlayer = (currentPlayer == PLAYER) ? AI : PLAYER;
        }
    }

    private boolean isValidMove(int row, int col, char player) {
        if (board[row][col] != EMPTY) {
            return false;
        }
        char opponent = (player == PLAYER) ? AI : PLAYER;
        return findFlippableDiscs(row, col, player, opponent).size() > 0;
    }

    private void flipDiscs(int row, int col, char player) {
        char opponent = (player == PLAYER) ? AI : PLAYER;
        List<int[]> flippableDiscs = findFlippableDiscs(row, col, player, opponent);
        for (int[] disc : flippableDiscs) {
            board[disc[0]][disc[1]] = player;
        }
    }

    private List<int[]> findFlippableDiscs(int row, int col, char player, char opponent) {
        List<int[]> flippableDiscs = new ArrayList<>();
        int[] directions = {-1, 0, 1};
        for (int dr : directions) {
            for (int dc : directions) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr;
                int c = col + dc;
                List<int[]> discs = new ArrayList<>();
                while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == opponent) {
                    discs.add(new int[]{r, c});
                    r += dr;
                    c += dc;
                }
                if (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
                    flippableDiscs.addAll(discs);
                }
            }
        }
        return flippableDiscs;
    }

    private boolean hasValidMove(char player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void makeAIMove() {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j, AI)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        if (!validMoves.isEmpty()) {
            int[] move = validMoves.get(new Random().nextInt(validMoves.size()));
            makeMove(move[0], move[1], AI);
        }
    }

    private int countDiscs(char player) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == player) {
                    count++;
                }
            }
        }
        return count;
    }
}
