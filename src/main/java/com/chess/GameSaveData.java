package com.chess;

import java.io.Serializable;
import java.util.List;

/**
 * 用于保存和恢复游戏状态的数据类
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Game> allGames;
    private int currentGameIndex;
    
    public GameSaveData() {
    }
    
    public List<Game> getAllGames() {
        return allGames;
    }
    
    public void setAllGames(List<Game> allGames) {
        this.allGames = allGames;
    }
    
    public int getCurrentGameIndex() {
        return currentGameIndex;
    }
    
    public void setCurrentGameIndex(int currentGameIndex) {
        this.currentGameIndex = currentGameIndex;
    }
} 