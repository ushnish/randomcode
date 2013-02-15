Random code projects I've done


TETRIS

 The tetris project consists of the following files
 1. TPoint.java
 2. Piece.java
 3. Board.java
 4. JTetris.java
 
These 3 files are all you need to play Tetris. Just run the JTetris file, 4 to move left, 5 to drop down, 6 to move right, 7 to rotate counter-clockwise and 9 to rotate clockwise. However if you'd like to see the computer play Tetris, you need these additional files. Run the JBrainTetris file and you have the option to make the brain active. Modify the DefaultBrain rateBoard method to improve the AI. The adversary slider has interesting effects sometimes...
 
 5. Brain.java
 6. DefaultBrain.java
 7. JBrainTetris.java
 
 
SUDOKU

 The Sudoku solver has these 2 files. Just run SudokuFrame and type out the Sudoku on the left side as a sequence of 81 numbers where 0's represent blanks, format and spacing does not matter as long as there are 81 numbers. Or you can copy some of the Sudoku grids in the Sudoku.java file and paste directly onto the Frame.
 1. Sudoku.java
 2. SudokuFrame.java



WEB LOADER

 The Web loader uses multi-threading to download files from an arbitrary list of websites. It only reads in data but doesn't actually create and write out files to the computer but that should be easy to implement within the WebWorker class, which is an extension of the Thread class. Just run WebFrame with the filename as the command-line argument, the file would be in the folder that contains the package.
 1. WebWorker.java
 2. WebTableModel.java
 3. WebFrame.java



Data Mining


APRIORI

 This code uses the Apriori algorithm to generate the top item association rules in terms of confidence, lift and conviction. A sample data file can be found at http://snap.stanford.edu/class/cs246-data/browsing.txt. It is not very efficient and might be improved.
 1. Apriori.java


MUTUAL FRIENDS

 Uses Map reduce to calculate friend recommendations for each person in a list of persons and friends based on number of mutual friends. Sample data: http://snap.stanford.edu/class/cs246-data/
hw1q1.zip
 1. MutualFriends.java


KMEANS

 Uses Map reduce to implement Kmeans algorithm for finding centroids of a set. Assumes a starting set of 10 centroids and iterates over the 10, makes strong assumptions about file name and directory structure so use with care. Sample data: http:
//snap.stanford.edu/class/cs246-data/hw2-q4-kmeans.zip
 1. Kmeans.java
