# 黑白棋游戏系统

## 项目说明
这是一个支持多局游戏管理的黑白棋游戏系统，包含三种游戏模式：
1. **Peace模式**：基础的棋子放置游戏，无需遵循黑白棋规则，使用8x8棋盘
2. **Reversi模式**：完整的黑白棋游戏，遵循标准黑白棋规则，使用8x8棋盘
3. **Gomoku模式**：五子棋游戏，先连成五子者获胜，使用15x15棋盘

## 游戏功能
- 支持三种游戏模式
- 图形用户界面，支持鼠标点击操作
- 可以通过游戏列表选择不同的游戏
- 可以添加新游戏到列表中
- 默认启动进入游戏1 (peace模式)
- 支持游戏状态保存和加载
- 允许退出程序并自动保存游戏状态

### 新增功能

#### 图形用户界面 (GUI)
- 使用JavaFX实现的现代化界面
- 左侧显示棋盘，支持鼠标点击落子
- 右侧显示游戏信息、游戏列表和操作按钮
- 合法落子位置高亮显示
- 游戏结束时自动显示结果对话框

#### 游戏重置功能
- 提供"重置游戏"按钮，可以初始化当前游戏进度
- 重置前会显示确认对话框，防止误操作
- 重置后保留游戏ID和类型，但清除所有已下的棋子

#### 扩展的五子棋（Gomoku模式）
- 棋盘大小扩展为15x15
- 行号采用16进制（1-F）
- 列号采用字母（A-O）
- 包含固定障碍物（位于3F、8G、9F、CK位置），无法在障碍物位置落子
- 炸弹功能：
  - 黑方初始有2个炸弹，白方初始有3个炸弹
  - 使用炸弹按钮进入炸弹模式，然后点击目标位置
  - 可以移除对方棋子并在该位置形成弹坑（符号@）
  - 弹坑位置无法落子

#### 文件回放功能
- 支持从文件读取命令序列并执行
- 点击"演示模式"按钮选择命令文件
- 每条命令执行后延迟1秒
- 文件执行完毕后显示提示对话框
- 支持测试文件：
  - test1.cmd：和平棋测试
  - test2.cmd：黑白棋测试
  - test3.cmd：五子棋测试

## 操作说明
- 点击棋盘格子：在指定位置落子
- 点击游戏列表中的游戏：切换到该游戏
- 点击"新建 Peace/Reversi/Gomoku"按钮：创建新游戏
- 点击"Pass"按钮：在Reversi模式下跳过当前回合
- 点击"使用炸弹"按钮：在Gomoku模式下激活炸弹模式
- 点击"重置游戏"按钮：重置当前游戏状态
- 点击"演示模式"按钮：从文件读取并执行命令序列
- 点击"退出"按钮：保存游戏状态并退出程序

## 编译与运行

### 使用Maven构建和运行
```
mvn clean javafx:run
```

### 手动编译
```
javac -encoding UTF-8 --module-path path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d target/classes src/main/java/com/chess/*.java
```

### 手动运行
```
java --module-path path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.chess.ChessGameApp
```

## 项目结构
- `src/main/java/com/chess/`：源代码目录
  - `Board.java`：棋盘类
  - `Piece.java`：棋子枚举类（包括空、黑、白、障碍物、弹坑）
  - `Player.java`：玩家类
  - `Game.java`：游戏抽象基类
  - `PeaceGame.java`：Peace模式游戏类
  - `ReversiGame.java`：Reversi模式游戏类
  - `GomokuGame.java`：Gomoku模式游戏类（五子棋）
  - `GameManager.java`：游戏管理器类
  - `ChessGameApp.java`：JavaFX主应用类，包含GUI界面和用户交互
  - `GameSaveData.java`：游戏存档数据类
- `pom.xml`：Maven项目配置文件
- `README.md`：项目说明文件

## 系统要求
- Java 17 或更高版本
- JavaFX 21 或更高版本
- Maven 3.6 或更高版本（如果使用Maven构建） 