package com.chess;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChessGameApp extends Application {
    private static final String SAVE_FILE = "pj.game";
    
    private GameManager gameManager;
    private BorderPane root;
    private GridPane chessboard;
    private VBox gameInfoPanel;
    private ListView<String> gameListView;
    private Button passButton;
    private Button bombButton;
    private Button resetButton;
    private Label statusLabel;
    private Label gameNumberLabel;
    private Label playerTurnLabel;
    private Label player1ScoreLabel;
    private Label player2ScoreLabel;
    private Label bombCountLabel;
    private boolean bombSelected = false;
    private boolean updatingGameList = false;
    
    @Override
    public void start(Stage primaryStage) {
        // 初始化游戏管理器
        gameManager = new GameManager();
        
        // 尝试加载保存的游戏状态
        loadGameState();
        
        // 创建主布局
        root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // 创建棋盘
        createChessboard();
        
        // 创建右侧信息面板
        createRightPanel();
        
        // 更新UI显示
        updateUI();
        
        // 设置场景
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("棋类游戏");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 注册窗口关闭事件，保存游戏状态
        primaryStage.setOnCloseRequest(event -> saveGameState());
    }
    
    private void createChessboard() {
        chessboard = new GridPane();
        chessboard.setAlignment(Pos.CENTER);
        chessboard.setHgap(2);
        chessboard.setVgap(2);
        chessboard.setPadding(new Insets(10));
        
        // 棋盘将在updateChessboard()中动态创建
        
        // 将棋盘添加到主布局左侧
        root.setLeft(chessboard);
    }
    
    private void createRightPanel() {
        // 创建右侧面板，分为三列
        HBox rightPanel = new HBox(10);
        rightPanel.setPadding(new Insets(10));
        
        // 第一列：游戏信息
        gameInfoPanel = new VBox(10);
        gameInfoPanel.setPadding(new Insets(10));
        gameInfoPanel.setAlignment(Pos.TOP_LEFT);
        
        gameNumberLabel = new Label("游戏编号: 1");
        playerTurnLabel = new Label("当前回合: 黑方");
        player1ScoreLabel = new Label("黑方得分: 0");
        player2ScoreLabel = new Label("白方得分: 0");
        bombCountLabel = new Label("剩余炸弹: 0");
        statusLabel = new Label("");
        
        passButton = new Button("Pass");
        passButton.setOnAction(e -> handlePassAction());
        
        bombButton = new Button("使用炸弹");
        bombButton.setOnAction(e -> handleBombButtonClick());
        
        resetButton = new Button("重置游戏");
        resetButton.setOnAction(e -> handleResetGame());
        
        gameInfoPanel.getChildren().addAll(
            gameNumberLabel, playerTurnLabel, 
            player1ScoreLabel, player2ScoreLabel, 
            bombCountLabel, statusLabel,
            passButton, bombButton, resetButton
        );
        
        // 第二列：游戏列表
        VBox gameListPanel = new VBox(10);
        gameListPanel.setPadding(new Insets(10));
        gameListPanel.setAlignment(Pos.TOP_LEFT);
        
        Label gameListLabel = new Label("游戏列表");
        gameListView = new ListView<>();
        gameListView.setPrefHeight(200);
        gameListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    String[] parts = newValue.split(" - ");
                    if (parts.length >= 1) {
                        int gameId = Integer.parseInt(parts[0].replace("Game ", ""));
                        gameManager.switchGame(gameId);
                        updateUI();
                    }
                }
            }
        );
        
        gameListPanel.getChildren().addAll(gameListLabel, gameListView);
        
        // 第三列：操作按钮
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.TOP_LEFT);
        
        Button newPeaceButton = new Button("新建 Peace");
        newPeaceButton.setOnAction(e -> {
            gameManager.addNewGame("peace");
            updateUI();
        });
        
        Button newReversiButton = new Button("新建 Reversi");
        newReversiButton.setOnAction(e -> {
            gameManager.addNewGame("reversi");
            updateUI();
        });
        
        Button newGomokuButton = new Button("新建 Gomoku");
        newGomokuButton.setOnAction(e -> {
            gameManager.addNewGame("gomoku");
            updateUI();
        });
        
        Button playbackButton = new Button("演示模式");
        playbackButton.setOnAction(e -> openPlaybackDialog());
        
        Button quitButton = new Button("退出");
        quitButton.setOnAction(e -> {
            saveGameState();
            Platform.exit();
        });
        
        controlPanel.getChildren().addAll(
            new Label("操作"),
            newPeaceButton, newReversiButton, newGomokuButton,
            playbackButton, quitButton
        );
        
        // 将三列添加到右侧面板
        rightPanel.getChildren().addAll(gameInfoPanel, gameListPanel, controlPanel);
        
        // 设置各列的宽度比例
        HBox.setHgrow(gameInfoPanel, Priority.ALWAYS);
        HBox.setHgrow(gameListPanel, Priority.ALWAYS);
        HBox.setHgrow(controlPanel, Priority.ALWAYS);
        
        // 将右侧面板添加到主布局
        root.setCenter(rightPanel);
    }
    
    private void updateChessboard() {
        chessboard.getChildren().clear();
        
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame == null) return;
        
        Board board = currentGame.getBoard();
        int size = board.getSize();
        
        // 添加列标签（A, B, C, ...）
        for (int col = 0; col < size; col++) {
            Label colLabel = new Label(Character.toString((char)('A' + col)));
            chessboard.add(colLabel, col + 1, 0);
        }
        
        // 添加行标签（1, 2, 3, ... 或 1-9, A-F）
        for (int row = 0; row < size; row++) {
            String rowLabel;
            if (row < 9) {
                rowLabel = Integer.toString(row + 1);
            } else {
                rowLabel = Character.toString((char)('A' + (row - 9)));
            }
            Label label = new Label(rowLabel);
            chessboard.add(label, 0, row + 1);
        }
        
        // 创建棋盘格子
        List<int[]> validMoves = currentGame.getValidMoves();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = createBoardCell(row, col, board.getPiece(row, col), isValidMove(validMoves, row, col));
                chessboard.add(cell, col + 1, row + 1);
            }
        }
    }
    
    private StackPane createBoardCell(int row, int col, Piece piece, boolean isValidMove) {
        StackPane cell = new StackPane();
        cell.setPrefSize(40, 40);
        
        // 设置棋盘格子样式
        String cellStyle = "-fx-border-color: black; -fx-border-width: 1;";
        if (isValidMove) {
            cellStyle += " -fx-background-color: lightgreen;";
        } else {
            cellStyle += " -fx-background-color: white;";
        }
        cell.setStyle(cellStyle);
        
        // 添加棋子或标记
        Label pieceLabel = new Label();
        switch (piece) {
            case BLACK:
                pieceLabel.setText("●");
                pieceLabel.setStyle("-fx-text-fill: black; -fx-font-size: 20;");
                break;
            case WHITE:
                pieceLabel.setText("○");
                pieceLabel.setStyle("-fx-text-fill: black; -fx-font-size: 20;");
                break;
            case BARRIER:
                pieceLabel.setText("#");
                pieceLabel.setStyle("-fx-text-fill: red; -fx-font-size: 20;");
                break;
            case CRATER:
                pieceLabel.setText("@");
                pieceLabel.setStyle("-fx-text-fill: brown; -fx-font-size: 20;");
                break;
            case EMPTY:
                pieceLabel.setText("");
                break;
        }
        
        cell.getChildren().add(pieceLabel);
        
        // 设置点击事件
        final int finalRow = row;
        final int finalCol = col;
        cell.setOnMouseClicked(event -> {
            handleCellClick(finalRow, finalCol);
        });
        
        return cell;
    }
    
    private boolean isValidMove(List<int[]> validMoves, int row, int col) {
        for (int[] move : validMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }
    
    private void handleCellClick(int row, int col) {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame == null || currentGame.isOver()) return;
        
        boolean success = false;
        
        if (bombSelected && currentGame instanceof GomokuGame) {
            // 使用炸弹
            success = gameManager.useBomb(row, col);
            bombSelected = false; // 重置炸弹选择状态
        } else {
            // 正常落子
            success = gameManager.placePiece(row, col);
        }
        
        if (success) {
            updateUI();
            
            // 检查游戏是否结束
            if (currentGame.isOver()) {
                showGameOverDialog();
            }
        }
    }
    
    private void handlePassAction() {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame instanceof ReversiGame) {
            if (gameManager.pass()) {
                updateUI();
                
                // 检查游戏是否结束
                if (currentGame.isOver()) {
                    showGameOverDialog();
                }
            } else {
                showAlert("错误", "当前有合法落子位置，无法执行pass！");
            }
        } else {
            showAlert("错误", "当前游戏模式不支持pass操作！");
        }
    }
    
    private void handleBombButtonClick() {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame instanceof GomokuGame) {
            Player player = currentGame.getCurrentPlayer();
            if (player.getBombCount() > 0) {
                bombSelected = true;
                bombButton.setStyle("-fx-background-color: #ff8080;");
                statusLabel.setText("请选择要使用炸弹的位置");
            } else {
                showAlert("错误", "没有足够的炸弹！");
            }
        } else {
            showAlert("错误", "当前游戏模式不支持炸弹操作！");
        }
    }
    
    private void handleResetGame() {
        // 显示确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("重置游戏");
        alert.setHeaderText(null);
        alert.setContentText("确定要重置当前游戏吗？这将清除所有已下的棋子。");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 用户确认重置
                gameManager.resetCurrentGame();
                updateUI();
                statusLabel.setText("游戏已重置");
            }
        });
    }
    
    private void updateUI() {
        updateChessboard();
        updateGameInfo();
        updateGameList();
    }
    
    private void updateGameInfo() {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame == null) return;
        
        // 更新游戏编号
        gameNumberLabel.setText("游戏编号: " + currentGame.getGameId());
        
        // 更新当前玩家
        Player currentPlayer = currentGame.getCurrentPlayer();
        String playerColor = (currentPlayer.getPiece() == Piece.BLACK) ? "黑方" : "白方";
        playerTurnLabel.setText("当前回合: " + playerColor);
        
        // 根据游戏类型显示或隐藏相关控件
        if (currentGame instanceof ReversiGame) {
            // Reversi模式显示得分
            ReversiGame reversiGame = (ReversiGame) currentGame;
            int blackScore = reversiGame.getScore(currentGame.getPlayer1());
            int whiteScore = reversiGame.getScore(currentGame.getPlayer2());
            
            player1ScoreLabel.setText("黑方得分: " + blackScore);
            player2ScoreLabel.setText("白方得分: " + whiteScore);
            player1ScoreLabel.setVisible(true);
            player2ScoreLabel.setVisible(true);
            
            // 显示Pass按钮，隐藏炸弹按钮和炸弹数量
            passButton.setVisible(true);
            bombButton.setVisible(false);
            bombCountLabel.setVisible(false);
            
        } else if (currentGame instanceof GomokuGame) {
            // Gomoku模式显示炸弹数量，隐藏得分
            player1ScoreLabel.setVisible(false);
            player2ScoreLabel.setVisible(false);
            
            // 显示炸弹按钮和炸弹数量，隐藏Pass按钮
            passButton.setVisible(false);
            bombButton.setVisible(true);
            bombCountLabel.setVisible(true);
            bombCountLabel.setText("剩余炸弹: " + currentPlayer.getBombCount());
            
        } else if (currentGame instanceof PeaceGame) {
            // Peace模式隐藏所有特殊控件
            player1ScoreLabel.setVisible(false);
            player2ScoreLabel.setVisible(false);
            passButton.setVisible(false);
            bombButton.setVisible(false);
            bombCountLabel.setVisible(false);
        }
        
        // 如果游戏结束，显示结果
        if (currentGame.isOver()) {
            String result = getGameResult();
            statusLabel.setText("游戏结束: " + result);
        } else {
            statusLabel.setText("");
        }
    }
    
    private void updateGameList() {
        if (updatingGameList) return;
        
        updatingGameList = true;
        List<Game> games = gameManager.getAllGames();
        gameListView.getItems().clear();
        
        for (Game game : games) {
            String gameType = game.getGameType();
            String gameInfo = "Game " + game.getGameId() + " - " + gameType;
            gameListView.getItems().add(gameInfo);
        }
        
        // 选中当前游戏
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame != null) {
            for (int i = 0; i < gameListView.getItems().size(); i++) {
                if (gameListView.getItems().get(i).startsWith("Game " + currentGame.getGameId())) {
                    gameListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
        updatingGameList = false;
    }
    
    private String getGameResult() {
        Game currentGame = gameManager.getCurrentGame();
        if (!currentGame.isOver()) return "";
        
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversiGame = (ReversiGame) currentGame;
            Player winner = reversiGame.getWinner();
            
            if (winner == null) {
                return "平局";
            } else {
                String color = (winner.getPiece() == Piece.BLACK) ? "黑方" : "白方";
                return color + "获胜";
            }
        } else if (currentGame instanceof GomokuGame) {
            GomokuGame gomokuGame = (GomokuGame) currentGame;
            Player winner = gomokuGame.getWinner();
            
            if (winner == null) {
                return "平局";
            } else {
                String color = (winner.getPiece() == Piece.BLACK) ? "黑方" : "白方";
                return color + "获胜";
            }
        } else if (currentGame instanceof PeaceGame) {
            return "游戏结束";
        }
        
        return "";
    }
    
    private void showGameOverDialog() {
        String result = getGameResult();
        showAlert("游戏结束", result);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void openPlaybackDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择指令文件");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("命令文件", "*.cmd")
        );
        
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            playbackCommands(file.getPath());
        }
    }
    
    private void playbackCommands(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // 创建一个定时执行器
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            
            // 读取所有命令
            List<String> commands = reader.lines().filter(line -> !line.trim().isEmpty()).toList();
            final int[] commandIndex = {0};
            
            // 显示正在执行演示模式
            statusLabel.setText("正在执行演示模式...");
            
            // 每隔1秒执行一条命令
            executor.scheduleAtFixedRate(() -> {
                if (commandIndex[0] < commands.size()) {
                    String command = commands.get(commandIndex[0]++);
                    
                    // 在JavaFX线程中执行UI更新
                    Platform.runLater(() -> {
                        executeCommand(command);
                        updateUI();
                    });
                } else {
                    // 命令执行完毕，关闭执行器
                    executor.shutdown();
                    
                    // 更新状态
                    Platform.runLater(() -> {
                        statusLabel.setText("演示模式结束");
                        showAlert("演示模式", "命令文件执行完毕！");
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
            
        } catch (IOException e) {
            showAlert("错误", "无法打开文件: " + filename);
        }
    }
    
    private void executeCommand(String command) {
        if (command.equalsIgnoreCase("pass")) {
            if (gameManager.getCurrentGame() instanceof ReversiGame) {
                gameManager.pass();
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
                    return;
                }
                
                int col = colChar - 'A';
                
                if (col < 0 || col >= gameManager.getCurrentGame().getBoard().getSize()) {
                    return;
                }
                
                gameManager.useBomb(row, col);
            } catch (Exception e) {
                // 忽略错误
            }
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
                    return;
                }
                
                char colChar = Character.toUpperCase(command.charAt(1));
                int col = colChar - 'A';
                
                if (col < 0 || col >= gameManager.getCurrentGame().getBoard().getSize()) {
                    return;
                }
                
                gameManager.placePiece(row, col);
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    private void saveGameState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // 创建要保存的数据
            GameSaveData saveData = new GameSaveData();
            saveData.setAllGames(gameManager.getAllGames());
            saveData.setCurrentGameIndex(gameManager.getCurrentGameIndex());
            
            // 写入数据
            out.writeObject(saveData);
        } catch (IOException e) {
            System.err.println("保存游戏状态失败: " + e.getMessage());
        }
    }
    
    private void loadGameState() {
        File saveFile = new File(SAVE_FILE);
        if (!saveFile.exists()) return;
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            // 读取保存的数据
            GameSaveData saveData = (GameSaveData) in.readObject();
            
            // 恢复游戏状态
            gameManager.setAllGames(saveData.getAllGames());
            gameManager.setCurrentGameIndex(saveData.getCurrentGameIndex());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载游戏状态失败: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 