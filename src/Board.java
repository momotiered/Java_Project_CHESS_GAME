public class Board {
    public static final int PEACE_REVERSI_SIZE = 8; // 和平棋和黑白棋使用8*8棋盘
    public static final int GOMOKU_SIZE = 15; // 五子棋使用15*15棋盘
    
    private Piece[][] board;
    private int size;

    public Board(int size) {
        this.size = size;
        board = new Piece[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void initPeaceBoard() {
        // 初始化Peace模式棋盘，中间四格有棋子
        board[3][3] = Piece.WHITE;
        board[3][4] = Piece.BLACK;
        board[4][3] = Piece.BLACK;
        board[4][4] = Piece.WHITE;
    }

    public void initReversiBoard() {
        // 初始化Reversi模式棋盘，初始状态：黑棋位于4E和5D，白棋位于4D和5E
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
        board[3][3] = Piece.WHITE; // 4D
        board[3][4] = Piece.BLACK; // 4E
        board[4][3] = Piece.BLACK; // 5D
        board[4][4] = Piece.WHITE; // 5E
    }

    public void initGomokuBoard() {
        // 初始化五子棋盘，设置障碍物
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
        
        // 添加固定障碍物（3F、8G、9F、CK）
        board[2][5] = Piece.BARRIER;  // 3F (第3行，第F列，索引为2,5)
        board[7][6] = Piece.BARRIER;  // 8G (第8行，第G列，索引为7,6)
        board[8][5] = Piece.BARRIER;  // 9F (第9行，第F列，索引为8,5)
        board[11][10] = Piece.BARRIER; // CK (第C行，第K列，索引为11,10)
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    public boolean isInBoard(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isEmpty(int row, int col) {
        return board[row][col] == Piece.EMPTY;
    }

    public boolean isBarrier(int row, int col) {
        return board[row][col] == Piece.BARRIER;
    }

    public boolean isCrater(int row, int col) {
        return board[row][col] == Piece.CRATER;
    }

    public boolean isPiece(int row, int col, Piece piece) {
        return board[row][col] == piece;
    }

    public int countPieces(Piece piece) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == piece) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == Piece.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
} 