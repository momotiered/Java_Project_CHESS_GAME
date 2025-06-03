package com.chess;

import java.util.ArrayList;
import java.util.List;

public class GomokuGame extends Game {
    private int moveCount;

    public GomokuGame(int gameId) {
        super(gameId, "Gomoku", Board.GOMOKU_SIZE);
        this.moveCount = 0;
        
        // 设置初始炸弹数量：白方3个，黑方2个
        player1.setBombCount(2); // 黑方2个炸弹
        player2.setBombCount(3); // 白方3个炸弹
        
        // 初始化五子棋盘，设置障碍物
        board.initGomokuBoard();
    }

    @Override
    public boolean placePiece(int row, int col) {
        if (row < 0 || row >= board.getSize() || col < 0 || col >= board.getSize()) {
            return false;
        }

        if (!board.isEmpty(row, col)) {
            return false;
        }

        if (board.isBarrier(row, col) || board.isCrater(row, col)) {
            return false;
        }

        board.setPiece(row, col, currentPlayer.getPiece());
        moveCount++;
        
        if (checkWin(row, col)) {
            gameOver = true;
            return true;
        }

        // 检查是否棋盘已满
        if (getValidMoves().isEmpty()) {
            gameOver = true;
            return true;
        }

        switchPlayer();
        return true;
    }
    
    public boolean useBomb(int row, int col) {
        // 检查是否在棋盘内
        if (row < 0 || row >= board.getSize() || col < 0 || col >= board.getSize()) {
            return false;
        }
        
        // 检查该位置是否有对方的棋子
        Piece opponentPiece = (currentPlayer == player1) ? player2.getPiece() : player1.getPiece();
        if (!board.isPiece(row, col, opponentPiece)) {
            return false;
        }
        
        // 尝试使用炸弹
        if (!currentPlayer.useBomb()) {
            return false; // 没有足够的炸弹
        }
        
        // 移除对方棋子，放置弹坑
        board.setPiece(row, col, Piece.CRATER);
        moveCount++;
        
        // 检查是否棋盘已满
        if (getValidMoves().isEmpty()) {
            gameOver = true;
        }
        
        switchPlayer();
        return true;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.isEmpty(i, j) && !board.isBarrier(i, j) && !board.isCrater(i, j)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    private boolean checkWin(int row, int col) {
        Piece currentPiece = currentPlayer.getPiece();
        
        // 检查水平方向
        if (checkDirection(row, col, 0, 1, currentPiece) >= 5) return true;
        // 检查垂直方向
        if (checkDirection(row, col, 1, 0, currentPiece) >= 5) return true;
        // 检查主对角线方向
        if (checkDirection(row, col, 1, 1, currentPiece) >= 5) return true;
        // 检查副对角线方向
        if (checkDirection(row, col, 1, -1, currentPiece) >= 5) return true;
        
        return false;
    }

    private int checkDirection(int row, int col, int deltaRow, int deltaCol, Piece piece) {
        int count = 1;
        
        // 正向检查
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < board.getSize() && c >= 0 && c < board.getSize() && 
               board.getPiece(r, c) == piece) {
            count++;
            r += deltaRow;
            c += deltaCol;
        }
        
        // 反向检查
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < board.getSize() && c >= 0 && c < board.getSize() && 
               board.getPiece(r, c) == piece) {
            count++;
            r -= deltaRow;
            c -= deltaCol;
        }
        
        return count;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public Player getWinner() {
        if (!gameOver) {
            return null;
        }
        
        // 检查棋盘是否已满
        if (getValidMoves().isEmpty()) {
            return null; // 平局
        }
        
        // 如果游戏结束且棋盘未满，说明有玩家获胜
        return currentPlayer;
    }
} 