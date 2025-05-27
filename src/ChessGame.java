import java.util.List;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class ChessGame {
    private GameManager gameManager;
    private Scanner scanner;
    
    public ChessGame() {
        gameManager = new GameManager();
        scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
    }
    
    public void start() {
        boolean quit = false;
        
        // 显示欢迎信息
        System.out.println("欢迎来到黑白棋游戏系统！");
        System.out.println("默认进入游戏1 (peace模式)");
        System.out.println("按回车键继续...");
        scanner.nextLine();
        
        while (!quit) {
            clearScreen();
            displayGame();
            
            System.out.print("请输入命令：");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit")) {
                quit = true;
            } else if (input.startsWith("playback ") && input.length() > 9) {
                // 处理playback命令
                String filename = input.substring(9).trim();
                playbackCommands(filename);
            } else if (input.matches("\\d+")) {  // 检查输入是否为数字
                // 直接通过数字切换游戏
                int gameId = Integer.parseInt(input);
                if (!gameManager.switchGame(gameId)) {
                    System.out.println("游戏编号不存在！");
                    waitForEnter();
                } else {
                    System.out.println("已切换到游戏" + gameId);
                    waitForEnter();
                }
            } else if (input.equalsIgnoreCase("peace") || input.equalsIgnoreCase("reversi") || input.equalsIgnoreCase("gomoku")) {
                // 添加新游戏到列表末尾
                gameManager.addNewGame(input);
                System.out.println("已添加并切换到新游戏: " + input);
                waitForEnter();
            } else if (input.equalsIgnoreCase("pass")) {
                if (gameManager.getCurrentGame() instanceof ReversiGame) {
                    if (!gameManager.pass()) {
                        System.out.println("当前有合法落子位置，无法执行pass！");
                        waitForEnter();
                    }
                } else {
                    System.out.println("当前游戏模式不支持pass操作！");
                    waitForEnter();
                }
            } else if (input.startsWith("@") && input.length() >= 3) {
                // 处理炸弹命令，例如：@FA
                processUseBombCommand(input);
            } else {
                // 解析落子坐标，例如：3D 或 AF
                processPlacePieceCommand(input);
            }
        }
        
        scanner.close();
    }
    
    private void playbackCommands(String filename) {
        // 尝试相对路径和绝对路径
        File file = new File(filename);
        
        // 如果文件不存在，尝试在当前目录中查找
        if (!file.exists()) {
            System.out.println("未找到文件: " + filename + "，尝试在当前目录中查找...");
            file = new File(".", filename);
        }
        
        // 如果还是找不到，输出错误信息
        if (!file.exists()) {
            System.out.println("找不到命令文件: " + filename);
            System.out.println("请确保文件位于程序执行目录中。");
            waitForEnter();
            return;
        }
        
        try (Scanner fileScanner = new Scanner(new FileInputStream(file), StandardCharsets.UTF_8.name())) {
            System.out.println("正在执行命令文件: " + filename);
            
            while (fileScanner.hasNextLine()) {
                String command = fileScanner.nextLine().trim();
                
                if (command.isEmpty()) {
                    continue; // 跳过空行
                }
                
                // 显示当前执行的命令
                clearScreen();
                displayGame();
                System.out.println("执行命令: " + command);
                
                // 执行命令
                executeCommand(command);
                
                // 延迟一秒
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            System.out.println("命令文件执行完毕！");
            waitForEnter();
        } catch (FileNotFoundException e) {
            System.out.println("无法打开文件: " + filename);
            System.out.println("错误信息: " + e.getMessage());
            waitForEnter();
        }
    }
    
    private void executeCommand(String command) {
        if (command.equalsIgnoreCase("pass")) {
            if (gameManager.getCurrentGame() instanceof ReversiGame) {
                if (!gameManager.pass()) {
                    System.out.println("当前有合法落子位置，无法执行pass！");
                }
            } else {
                System.out.println("当前游戏模式不支持pass操作！");
            }
        } else if (command.startsWith("@") && command.length() >= 3) {
            // 处理炸弹命令
            processUseBombCommand(command);
        } else {
            // 假设是落子坐标
            processPlacePieceCommand(command);
        }
    }
    
    private void processUseBombCommand(String command) {
        if (gameManager.getCurrentGame() instanceof GomokuGame) {
            try {
                char rowChar = Character.toUpperCase(command.charAt(1));
                char colChar = Character.toUpperCase(command.charAt(2));
                
                int row;
                // 处理16进制行号1-F
                if (rowChar >= '1' && rowChar <= '9') {
                    row = rowChar - '1';
                } else if (rowChar >= 'A' && rowChar <= 'F') {
                    row = rowChar - 'A' + 9;
                } else {
                    System.out.println("行号必须是1-9或A-F之间的字符！");
                    return;
                }
                
                int col = colChar - 'A';
                
                if (col < 0 || col >= gameManager.getCurrentGame().getBoard().getSize()) {
                    System.out.println("列号超出范围！");
                    return;
                }
                
                if (!gameManager.useBomb(row, col)) {
                    System.out.println("无法使用炸弹！可能是因为：1.没有足够的炸弹 2.目标位置没有对方棋子");
                }
            } catch (Exception e) {
                System.out.println("炸弹命令格式错误！请使用如 @3F 或 @AF 的格式。");
            }
        } else {
            System.out.println("当前游戏模式不支持炸弹操作！");
        }
    }
    
    private void processPlacePieceCommand(String command) {
        try {
            if (command.length() >= 2) {
                int row;
                char rowChar = Character.toUpperCase(command.charAt(0));
                
                // 处理16进制行号1-F
                if (rowChar >= '1' && rowChar <= '9') {
                    row = rowChar - '1';
                } else if (rowChar >= 'A' && rowChar <= 'F') {
                    row = rowChar - 'A' + 9;
                } else {
                    System.out.println("行号必须是1-9或A-F之间的字符！");
                    return;
                }
                
                char colChar = Character.toUpperCase(command.charAt(1));
                int col = colChar - 'A';
                
                if (col < 0 || col >= gameManager.getCurrentGame().getBoard().getSize()) {
                    System.out.println("列号超出范围！");
                    return;
                }
                
                if (!gameManager.placePiece(row, col)) {
                    System.out.println("无效的落子位置！");
                }
            } else {
                System.out.println("命令格式错误！");
            }
        } catch (Exception e) {
            System.out.println("输入格式错误！请使用如 3F 或 AF 的格式或其他有效命令。");
        }
    }
    
    private void displayGame() {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame == null) {
            System.out.println("没有可用的游戏！");
            return;
        }
        
        Board board = currentGame.getBoard();
        List<int[]> validMoves = currentGame.getValidMoves();
        int boardSize = board.getSize();
        
        // 构建标题行，在Reversi模式下显示得分
        String scoreInfo = "";
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversiGame = (ReversiGame) currentGame;
            int blackScore = reversiGame.getScore(currentGame.getPlayer1());
            int whiteScore = reversiGame.getScore(currentGame.getPlayer2());
            scoreInfo = String.format("黑方得分: %d  白方得分: %d", blackScore, whiteScore);
        }
        
        // 获取所有游戏列表
        List<Game> allGames = gameManager.getAllGames();
        
        // 显示列号 A-O 或 A-H
        System.out.print("  ");
        for (int j = 0; j < boardSize; j++) {
            System.out.print((char)('A' + j) + " ");
        }
        System.out.println("   游戏信息             游戏列表");
        
        // 分隔线
        System.out.print("  ");
        for (int j = 0; j < boardSize; j++) {
            System.out.print("--");
        }
        System.out.println("   ----------------    ----------------");
        
        // 显示棋盘内容和行号
        for (int i = 0; i < boardSize; i++) {
            // 显示行号（8*8棋盘用1-8，15*15棋盘用16进制1-F）
            char rowChar;
            if (boardSize <= 9) {
                rowChar = (char)('1' + i);
            } else {
                rowChar = (i < 9) ? (char)('1' + i) : (char)('A' + (i - 9));
            }
            System.out.print(rowChar + " ");
            
            for (int j = 0; j < boardSize; j++) {
                if (currentGame.getGameType().equals("reversi") && isValidMove(validMoves, i, j)) {
                    System.out.print("+ ");
                } else {
                    System.out.print(board.getPiece(i, j).getSymbol() + " ");
                }
            }
            
            // 中间显示游戏状态
            if (i == 0) {
                System.out.print("   游戏编号: " + currentGame.getGameId());
            } else if (i == 1) {
                System.out.print("   游戏类型: " + currentGame.getGameType());
            } else if (i == 2) {
                Player player1 = currentGame.getPlayer1();
                System.out.print("   " + player1.getName() + ": " + player1.getPiece().getSymbol());
                if (currentGame.getCurrentPlayer() == player1) {
                    System.out.print(" ←");
                }
                // 显示黑方炸弹数量
                if (currentGame instanceof GomokuGame) {
                    System.out.print(" 炸弹: " + player1.getBombCount());
                }
            } else if (i == 3) {
                Player player2 = currentGame.getPlayer2();
                System.out.print("   " + player2.getName() + ": " + player2.getPiece().getSymbol());
                if (currentGame.getCurrentPlayer() == player2) {
                    System.out.print(" ←");
                }
                // 显示白方炸弹数量
                if (currentGame instanceof GomokuGame) {
                    System.out.print(" 炸弹: " + player2.getBombCount());
                }
            } else if (i == 4 && currentGame instanceof ReversiGame) {
                System.out.print("   " + scoreInfo);
            } else if (i == 4 && currentGame instanceof GomokuGame) {
                GomokuGame gomokuGame = (GomokuGame) currentGame;
                System.out.print("   当前轮数: " + gomokuGame.getMoveCount());
            }
            
            // 右侧显示游戏列表
            if (i == 0) {
                System.out.println("    游戏列表:");
            } else if (i < allGames.size() + 1) {
                Game game = allGames.get(i - 1);
                System.out.println("    " + game.getGameId() + ". " + game.getGameType());
            } else {
                System.out.println();
            }
        }
        
        // 显示游戏结束信息
        if (currentGame.isOver()) {
            System.out.println("\n游戏结束！");
            if (currentGame instanceof ReversiGame) {
                ReversiGame reversiGame = (ReversiGame) currentGame;
                Player winner = reversiGame.getWinner();
                if (winner != null) {
                    System.out.println(winner.getName() + " 获胜！");
                } else {
                    System.out.println("平局！");
                }
            } else if (currentGame instanceof GomokuGame) {
                GomokuGame gomokuGame = (GomokuGame) currentGame;
                Player winner = gomokuGame.getWinner();
                if (winner != null) {
                    System.out.println(winner.getName() + " 获胜！");
                } else {
                    System.out.println("平局！");
                }
            }
        }
        
        System.out.println("\n命令: [坐标] - 落子 (如3F或AF), 数字 - 切换游戏, peace/reversi/gomoku - 添加新游戏, pass - 跳过, quit - 退出");
        System.out.println("playback filename.cmd - 从文件读取并执行命令序列");
        if (currentGame.getGameType().equals("gomoku")) {
            System.out.println("注意: # 表示障碍物，无法在障碍物位置落子。@ 表示弹坑，同样无法落子。");
            System.out.println("炸弹使用方法: @行列 (如@3F) - 可以移除对方的一颗棋子并在该位置形成弹坑");
        }
    }
    
    private boolean isValidMove(List<int[]> validMoves, int row, int col) {
        for (int[] move : validMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }
    
    private void clearScreen() {
        try {
            // 根据操作系统选择不同的清屏命令
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("windows")) {
                // Windows系统使用cls命令清屏
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Linux/Mac系统使用clear命令清屏
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            // 如果清屏命令执行失败，退回到使用换行的方式
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private void waitForEnter() {
        System.out.println("按回车键继续...");
        scanner.nextLine();
    }
    
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.start();
    }
} 