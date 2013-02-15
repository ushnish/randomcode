// Board.java
package tetris;

/**
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width, height;
	
	private boolean[][] grid;
	private boolean[][] backupGrid;
	private int[] rowWidths, colHeights, backupRowWidths, backupColHeights;
	private boolean[] rowsToClear, backupRowsToClear;		
	
	private boolean DEBUG = false;

	boolean committed;	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		
		grid = new boolean[width][height];
		rowsToClear = new boolean[height];
		rowWidths = new int[height];
		colHeights = new int[width];		
				
		backupGrid = new boolean[width][height];
		backupRowsToClear = new boolean[height];
		backupRowWidths = new int[height];
		backupColHeights = new int[width];
		
		committed = true;
		// YOUR CODE HERE
	}
	
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		int maxHeight = 0;
		for (int x = 0; x < width; x++) {
			if (maxHeight < colHeights[x]) maxHeight = colHeights[x];
		}
		return maxHeight;
		// YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	// always checks the grid attributes match with array attributes (col Height, row width, filled rows) and backup grid against the backup arrays
	public void sanityCheck() {
		if (DEBUG) {
			// check if the grid rows have the same number of trues as each entry of the colHeights array
			int numBlocks;
			for (int y = 0; y < height; y++) {
				numBlocks = 0;
				for (int x = 0; x < width; x++) {
					if (grid[x][y]) numBlocks++;
				}
				if (numBlocks != rowWidths[y]) throw new RuntimeException("Sanity check failed row Width check");
			}
			// check if the grid columns at each x has the same "height" as the height array
			for (int x = 0; x < width; x++) {
				if (colHeights[x] != calcColHeight(x, grid)) throw new RuntimeException("Sanity check failed col Height check");
			}
			// check if the boolean rowsToClear array has trues if and only if the widths array has entry = grid width
			for (int y = 0; y < height; y++) {
				if ((rowsToClear[y] && rowWidths[y] != width) || (!rowsToClear[y] && rowWidths[y] == width)) { 
					throw new RuntimeException("Rows to clear does not match row widths");
				}
			}
			// check if the backupGrid rows have the same number of trues as each entry of the colHeights array
			
			for (int y = 0; y < height; y++) {
				numBlocks = 0;
				for (int x = 0; x < width; x++) {
					if (backupGrid[x][y]) numBlocks++;
				}
				if (numBlocks != backupRowWidths[y]) throw new RuntimeException("Sanity check failed backup row Width check");
			}
			// check if the grid columns at each x has the same "height" as the height array
			for (int x = 0; x < width; x++) {
				if (backupColHeights[x] != calcColHeight(x, backupGrid)) throw new RuntimeException("Sanity check failed col Height check");
			}
			// check if the boolean rowsToClear array has trues if and only if the widths array has entry = grid width
			for (int y = 0; y < height; y++) {
				if ((backupRowsToClear[y] && backupRowWidths[y] != width) || (!backupRowsToClear[y] && backupRowWidths[y] == width)) { 
					throw new RuntimeException("Rows to clear does not match row widths");
				}
			}
//			if the board is in a committed state, all the arrays should exactly equal their backups
			if (committed) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (grid[x][y] != backupGrid[x][y]) throw new RuntimeException("Sanity check: Grid not backed up correctly");
					}
				}
				for (int y = 0; y < height; y++) {
					if (rowsToClear[y] != backupRowsToClear[y]) throw new RuntimeException("Sanity check: Rows to clear not backed up correctly");
					if (rowWidths[y] != backupRowWidths[y]) throw new RuntimeException("Sanity check: Row widths not backed up correctly");
				}
				for (int x = 0; x < width; x++) {
					if (backupColHeights[x] != colHeights[x]) throw new RuntimeException("Sanity check: Col Heights not backed up correctly");
				}
				
			}
			// YOUR CODE HERE
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int currHeight, colHeight;
		int dropHeight = 0;
		int[] skirt = piece.getSkirt();
		int pieceWidth = piece.getWidth();
		if (x + pieceWidth > width) throw new RuntimeException("Invalid starting x coordinate");
		for (int i = 0; i < pieceWidth; i++) {
			colHeight = getColumnHeight(x + i);
			currHeight = colHeight - skirt[i];
			if (dropHeight < currHeight) dropHeight = currHeight;
		}
		return dropHeight; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return colHeights[x];// YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return rowWidths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if (x < width && x >= 0 && y < height && y >= 0) return grid[x][y];
		else return true; // YOUR CODE HERE
	}
	
	private int max(int x, int y) {
		if (x > y) return x;
		return y;
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("Place called in uncommited state");

		int result = PLACE_OK;

		TPoint[] points = piece.getBody();
		TPoint point;
		int numPoints = points.length;
//		justPlacedBlocks = new TPoint[numPoints];
		
		int yPos, xPos;
		if (piece.getWidth() + x - 1 < width && piece.getHeight() + y - 1 < height) {
			for (int i = 0; i < numPoints; i++) {
				point = points[i];
				yPos= point.y + y;
				xPos = point.x + x;
				if (getGrid(xPos, yPos)) {
					result = PLACE_BAD;
					return result;
				}
				// commit becomes false if and only if a piece was actually added, otherwise no need for undo or commit() to do anything
				committed = false;	
				grid[xPos][yPos] = true;
				// increment row width at that y value
				rowWidths[yPos] = rowWidths[yPos] + 1;  
				// increment column width at that x value as max(previous height, y coordinate + 1)
				colHeights[xPos] = max(colHeights[xPos], yPos+1);
				// scan the row which was affected by this block to see if it was filled
				if (width == rowWidths[yPos]) {
					rowsToClear[yPos] = true;
					result = PLACE_ROW_FILLED;
				}
			}
		}
		else result = PLACE_OUT_BOUNDS;

		sanityCheck();
		// YOUR CODE HERE		
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		int maxRow = getMaxHeight()-1;
		for (int y = 0; y <= maxRow; y++){
			if (rowsToClear[y]) {
				shiftDown(y, maxRow);
				shiftDownAll(y, maxRow);
				maxRow--;
				y--;
				rowsCleared++;
			}
		}
		updateColHeights();
//		justCleared = true;

		committed = false;
		sanityCheck();
		return rowsCleared;
		// YOUR CODE HERE
	}
	
	private void copyRow(int copyFrom, int copyTo) {
		for (int x = 0; x < width; x++) {
			grid[x][copyTo] = grid[x][copyFrom];
		}
	}
	
	private void shiftDownAll(int startRow, int maxRow) {
		for (int row = startRow; row < maxRow; row++) {
			copyRow(row+1, row);
		}
		for (int x = 0; x < width; x++) {
			grid[x][maxRow] = false;
		}
	}
	
	// shifts down the boolean array and the widths array and introduces false and 0 at the top
	private void shiftDown(int rowTrue, int maxRow) {
		for (int i = rowTrue; i < maxRow; i++) {
			rowsToClear[i] = rowsToClear[i+1];
			rowWidths[i] = rowWidths[i+1];
		}
		rowsToClear[maxRow] = false;
		rowWidths[maxRow] = 0;
	}
	
	// calculate the height of the grid at x to update the colHeights array, only called during a clearRows operation
	private int calcColHeight(int x, boolean[][] grid1) {
		int colHeight = height;
		while (colHeight > 0) {
			if (grid1[x][colHeight-1]) return colHeight;
			colHeight--;
		}
		return colHeight;
	}
	
	// updates the colHeights array, only during clearrows
	private void updateColHeights() {
		for (int x = 0; x < width; x++) {
			colHeights[x] = calcColHeight(x, grid);
		}
	}


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	
	// if undo is called, swap the pointers for all the arrays and call commit
	public void undo() {
		if (!committed) {
			boolean[][] tempGrid = grid;
			grid = backupGrid;
			backupGrid = tempGrid;
			
			int[] temp1 = rowWidths;
			rowWidths = backupRowWidths;
			backupRowWidths = temp1;
			
			boolean[] temp2 = rowsToClear;
			rowsToClear = backupRowsToClear;
			backupRowsToClear = temp2;
			
			int[] temp3 = colHeights;
			colHeights = backupColHeights;
			backupColHeights = temp3;
			
			commit();					
		}
		sanityCheck();
		// YOUR CODE HERE
	}
	


	/**
	 Puts the board in the committed state.
	*/
	
	public void commit() {
		if (!committed) {
			backUp();
			committed = true;
		}			
		sanityCheck();
	}

	// backs up all the arrays by copying to the backup when committing commit
	public void backUp() {
		for (int y = 0; y < grid.length; y++) {
			copyFromTo(grid[y], backupGrid[y]);
		}
		copyFromTo(rowWidths, backupRowWidths);
		copyFromTo(colHeights, backupColHeights);
		copyFromTo(rowsToClear, backupRowsToClear);
	}
	
	// copies one array to another
	public void copyFromTo(int[] from, int[] to) {
		System.arraycopy(from, 0, to, 0, from.length);
	}
	public void copyFromTo(boolean[] from, boolean[] to) {
		System.arraycopy(from, 0, to, 0, from.length);
	}

	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


