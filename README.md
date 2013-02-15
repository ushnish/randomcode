Random code projects I've done

TETRIS
 The tetris project consists of the following files
 1. TPoint.java
 2. Piece.java
 3. Board.java
 4. JTetris.java
 
These 3 files are all you need to play Tetris. Just run the JTetris file, 4 to move left, 5 to drop down, 6 to move right, 7 to rotate counter-clockwise and 9 to rotate clockwise. However if you'd like to see the computer play Tetris, you need these additional files. Run the JBrainTetris file and you have the option to make the brain active. The adversary slider has interesting effects sometimes...
 
 5. Brain.java
 6. DefaultBrain.java
 7. JBrainTetris.java
 
SUDOKU
 The Sudoku solver has these 2 files. Just run SudokuFrame and type out the Sudoku on the left side as a sequence of 81 numbers where 0's represent blanks, format and spacing does not matter as long as there are 81 numbers.
 1. Sudoku.java
 2. SudokuFrame.java

WEB LOADER
 The Web loader uses multi-threading to download files from an arbitrary list of websites. It only reads in data but doesn't actually create and write out files to the computer but that should be easy to implement within the WebWorker class, which is an extension of the Thread class. Just run WebFrame with the filename as the command-line argument, the file would be in the folder that contains the package.
 1. WebWorker.java
 2. WebTableModel.java
 3. WebFrame.java
