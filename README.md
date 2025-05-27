# 黑白棋游戏系统

## 项目说明
这是一个支持多局游戏管理的黑白棋游戏系统，包含三种游戏模式：
1. **Peace模式**：基础的棋子放置游戏，无需遵循黑白棋规则，使用8x8棋盘
2. **Reversi模式**：完整的黑白棋游戏，遵循标准黑白棋规则，使用8x8棋盘
3. **Gomoku模式**：五子棋游戏，先连成五子者获胜，使用15x15棋盘

## 游戏功能
- 支持三种游戏模式
- 可以通过输入1、2或3直接切换游戏
- 可以添加新游戏到列表中（输入peace、reversi或gomoku）
- 默认启动进入游戏1 (peace模式)
- 允许退出程序（输入quit）

### 新增功能

#### 扩展的五子棋（Gomoku模式）
- 棋盘大小扩展为15x15
- 行号采用16进制（1-F）
- 列号采用字母（A-O）
- 包含固定障碍物（位于3F、8G、9F、CK位置），无法在障碍物位置落子
- 炸弹功能：
  - 黑方初始有2个炸弹，白方初始有3个炸弹
  - 使用格式：@行列（如@3F）
  - 可以移除对方棋子并在该位置形成弹坑（符号@）
  - 弹坑位置无法落子

#### 文件回放功能
- 支持从文件读取命令序列并执行：`playback filename.cmd`
- 每条命令执行后延迟1秒
- 文件执行完毕后返回键盘输入模式
- 支持测试文件：
  - test1.cmd：和平棋测试
  - test2.cmd：黑白棋测试
  - test3.cmd：五子棋测试

## 命令说明
- `[坐标]`：如 3D 或 AF，表示在指定位置落子
- `1`：切换到游戏1 (peace模式)
- `2`：切换到游戏2 (reversi模式)
- `3`：切换到游戏3 (gomoku模式)
- `peace`/`reversi`/`gomoku`：添加新游戏到列表末尾
- `pass`：在Reversi模式下，当没有合法落子位置时，跳过当前回合
- `@行列`：在Gomoku模式下使用炸弹（如@3F）
- `playback filename.cmd`：从文件读取并执行命令序列
- `quit`：退出游戏

## 编译与运行

### 编译
```
javac -encoding UTF-8 -d bin src/*.java
```

### 运行

Windows PowerShell:
```
$env:JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"; java -cp bin ChessGame
```

Windows CMD:
```
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
java -cp bin ChessGame
```

Linux/Mac系统:
```
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
java -cp bin ChessGame
```

### 解决乱码问题
如果遇到中文乱码，请确保：
1. 使用`-encoding UTF-8`参数编译
2. 设置JAVA_TOOL_OPTIONS环境变量指定UTF-8编码
3. 确保控制台/终端支持UTF-8编码

## 项目结构
- `src/`：源代码目录
  - `Board.java`：棋盘类
  - `Piece.java`：棋子枚举类（包括空、黑、白、障碍物、弹坑）
  - `Player.java`：玩家类
  - `Game.java`：游戏抽象基类
  - `PeaceGame.java`：Peace模式游戏类
  - `ReversiGame.java`：Reversi模式游戏类
  - `GomokuGame.java`：Gomoku模式游戏类（五子棋）
  - `GameManager.java`：游戏管理器类
  - `ChessGame.java`：主应用类，包含界面和用户交互
- `bin/`：编译后的类文件目录
- `README.md`：项目说明文件 