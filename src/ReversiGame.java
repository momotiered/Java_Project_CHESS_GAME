import java.util.ArrayList;
import java.util.List;

public class ReversiGame extends Game {
    private boolean lastMovePass;
    
    // 方向数组，用于检查8个方向
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };
    
    public ReversiGame(int gameId) {
        super(gameId, "reversi", Board.PEACE_REVERSI_SIZE);
        lastMovePass = false;
        board.initReversiBoard();
    }

    @Override
    public boolean placePiece(int row, int col) {
        // 检查是否是合法的落子位置
        if (!isValidMove(row, col, currentPlayer.getPiece())) {
            return false;
        }
        
        // 放置棋子
        board.setPiece(row, col, currentPlayer.getPiece());
        
        // 翻转对手的棋子
        flipPieces(row, col);
        
        // 重置pass标志
        lastMovePass = false;
        
        // 检查游戏是否结束
        if (isGameOver()) {
            gameOver = true;
            return true;
        }
        
        // 切换玩家
        switchPlayer();
        
        // 检查下一个玩家是否有合法落子位置
        if (getValidMoves().isEmpty()) {
            // 如果没有合法落子位置，执行pass
            lastMovePass = true;
            switchPlayer(); // 切换回上一个玩家
            
            // 再次检查游戏是否结束（如果双方都没有合法落子位置）
            if (getValidMoves().isEmpty()) {
                gameOver = true;
            }
        }
        
        return true;
    }
    
    public boolean pass() {
        // 只有当前玩家没有合法落子位置时才允许pass
        if (!getValidMoves().isEmpty()) {
            return false;
        }
        
        // 如果上一步也是pass，游戏结束
        if (lastMovePass) {
            gameOver = true;
            return true;
        }
        
        lastMovePass = true;
        switchPlayer();
        
        // 检查下一个玩家是否有合法落子位置
        if (getValidMoves().isEmpty()) {
            gameOver = true;
        }
        
        return true;
    }
    
    private void flipPieces(int row, int col) {
        Piece currentPiece = currentPlayer.getPiece();
        Piece opponentPiece = (currentPiece == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        
        // 检查8个方向
        for (int[] dir : DIRECTIONS) {
            int dr = dir[0];
            int dc = dir[1];
            int r = row + dr;
            int c = col + dc;
            
            // 暂存可能需要翻转的位置
            List<int[]> toFlip = new ArrayList<>();
            
            // 沿着当前方向移动
            while (board.isInBoard(r, c) && board.getPiece(r, c) == opponentPiece) {
                toFlip.add(new int[]{r, c});
                r += dr;
                c += dc;
            }
            
            // 如果这个方向上最后一个是自己的棋子，翻转中间所有对手的棋子
            if (board.isInBoard(r, c) && board.getPiece(r, c) == currentPiece && !toFlip.isEmpty()) {
                for (int[] pos : toFlip) {
                    board.setPiece(pos[0], pos[1], currentPiece);
                }
            }
        }
    }
    
    public boolean isValidMove(int row, int col, Piece piece) {
        // 首先检查位置是否为空
        if (!board.isEmpty(row, col)) {
            return false;
        }
        
        Piece opponentPiece = (piece == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        boolean validMove = false;
        
        // 检查8个方向
        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0];
            int c = col + dir[1];
            boolean hasOpponentPiece = false;
            
            // 检查这个方向上是否有对手的棋子
            while (board.isInBoard(r, c) && board.getPiece(r, c) == opponentPiece) {
                r += dir[0];
                c += dir[1];
                hasOpponentPiece = true;
            }
            
            // 如果这个方向上有对手的棋子，并且最后一个棋子是自己的，则这是一个有效的落子位置
            if (hasOpponentPiece && board.isInBoard(r, c) && board.getPiece(r, c) == piece) {
                validMove = true;
                break;
            }
        }
        
        return validMove;
    }

    @Override
    public boolean isGameOver() {
        // Reversi模式下，游戏结束条件：棋盘满或双方都无合法落子位置
        if (board.isFull()) {
            return true;
        }
        
        // 检查双方是否都没有合法落子位置
        boolean player1HasMoves = false;
        boolean player2HasMoves = false;
        
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.isEmpty(i, j)) {
                    if (isValidMove(i, j, player1.getPiece())) {
                        player1HasMoves = true;
                    }
                    if (isValidMove(i, j, player2.getPiece())) {
                        player2HasMoves = true;
                    }
                }
            }
        }
        
        return !player1HasMoves && !player2HasMoves;
    }

    @Override
    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (isValidMove(row, col, currentPlayer.getPiece())) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }
        
        return validMoves;
    }
    
    public int getScore(Player player) {
        return board.countPieces(player.getPiece());
    }
    
    public Player getWinner() {
        if (!gameOver) {
            return null;
        }
        
        int score1 = getScore(player1);
        int score2 = getScore(player2);
        
        if (score1 > score2) {
            return player1;
        } else if (score2 > score1) {
            return player2;
        } else {
            return null; // 平局
        }
    }
} 