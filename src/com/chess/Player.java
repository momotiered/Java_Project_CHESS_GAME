package com.chess;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Piece piece;
    private int bombCount; // 玩家拥有的炸弹数量

    public Player(String name, Piece piece) {
        this.name = name;
        this.piece = piece;
        this.bombCount = 0; // 默认没有炸弹
    }

    public Player(String name, Piece piece, int bombCount) {
        this.name = name;
        this.piece = piece;
        this.bombCount = bombCount;
    }

    public String getName() {
        return name;
    }

    public Piece getPiece() {
        return piece;
    }
    
    public int getBombCount() {
        return bombCount;
    }
    
    public void setBombCount(int count) {
        this.bombCount = count;
    }
    
    public boolean useBomb() {
        if (bombCount > 0) {
            bombCount--;
            return true;
        }
        return false;
    }
} 