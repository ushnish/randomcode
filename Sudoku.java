package assign3;

import java.util.*;


/*
 * Encapsulates a Sudoku grid to be solved.
 */
public class Sudoku {
  // Provided grid data for main/testing
	// The instance variable strategy is up to you.
	private int[][] grid;
	// the current solvedGrid which will be erased when attempting to find the next solution
	private int[][] solvedGrid;
	// the very first solved grid is saved separately
	private int[][] finalGrid;
	private boolean saved;
	// list of empty spots
	private LinkedList<Spot> spots;
	private long startTime;
	private long endTime;
	private int numSolutions;
		
	private final int[] digits = {1,2,3,4,5,6,7,8,9};
	
	public class Spot {
		// the position of the spot in the grid
		private int row;
		private int col;
		// the set of possible integers at this spot
		private HashSet<Integer> solutions;
		
		public Spot(int row, int col) {
			this.row = row;
			this.col = col;
			solutions = new HashSet<Integer>();
		}
		// set the value of the solvedGrid and remove this number from the set of solutions at that spot
		public void set(int value) {
			solvedGrid[row][col] = value;
		}
		// reset the value of the solvedGrid to 0 and add this number to the set of solutions at that spot
		public void unset(int value) {
			solvedGrid[row][col] = 0;
		}
		// set the value of the spot into the solvedGrid, called only if the spot has only 1 possible value
		public void setVal() {
			Iterator<Integer> itr = solutions.iterator();
			set(itr.next());
		}
		
		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append(row + " " + col + "\n");
			Iterator<Integer> itr = solutions.iterator();
			while (itr.hasNext()) {
				buff.append(" "+ itr.next() + " ");
			}
			buff.append("\n");
			return (buff.toString());
		}
	}
	
	// sort the list of spots by the size of each spot's solution set
	private void spotSort(LinkedList<Spot> spots) {
		Collections.sort(spots, new Comparator<Spot>() {
			  public int compare(Spot spot1, Spot spot2) {
				  return (spot1.solutions.size() - spot2.solutions.size());
			  }
			});
	}
	// returns true if the value is not in that column of the solved grid
	private boolean notInCol(int value, int col) {
		for (int i = 0; i < SIZE; i++) {
			if (solvedGrid[i][col] == value) {
				return false;
			}
		}
		return true;
	}
		
	// returns true if the value is not in that row of the solved grid
	private boolean notInRow(int value, int row) {
		for (int i = 0; i < SIZE; i++) {
			if (solvedGrid[row][i] == value) {
				return false;
			}
		}
		return true;
	}
	
	// returns true if the value is not in the corresponding square of the grid, given the row and col w.r.t the full grid
	private boolean notInSquare(int value, int row, int col) {
		// the indices of the first element of the square corresponding to this row and col
		int sqrRow = ((int) row/PART)*PART;
		int sqrCol = ((int) col/PART)*PART;
		for (int i = 0; i < PART; i++) {
			for (int j = 0; j < PART; j++) {
				if (solvedGrid[sqrRow+i][sqrCol+j] == value) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		grid = new int[SIZE][SIZE];
		solvedGrid = new int[SIZE][SIZE];
		finalGrid = new int[SIZE][SIZE];
		saved = false;
		spots = new LinkedList<Spot>();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				grid[i][j] = ints[i][j];
				solvedGrid[i][j] = ints[i][j];
				if (grid[i][j] == 0) {
					Spot spot = new Spot(i, j);
					spots.add(spot);				
				}
			}
		}
		// YOUR CODE HERE
	}
	
	// save the solved grid in final grid
	private void saveSolved() {
		if(!saved) {
			for (int i = 0; i < SIZE; i++) {
				System.arraycopy(solvedGrid[i], 0, finalGrid[i], 0, solvedGrid[i].length);
			}
			saved = true;
		}
	}
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	
	// calculates all the solutions of a spot based on the current solvedGrid and adds them to the solutions set, if there is only one solution set that value in the solvedGrid
	private void spotSolutions(Spot spot) {
		int row = spot.row;
		int col = spot.col;
		HashSet<Integer> solutions = spot.solutions;
		for (int value : digits) {
			// if this digit is not in the row column of square of that spot add it
			if (notInCol(value, col) && notInRow(value, row) && notInSquare(value, row, col)) {
				solutions.add(value);
			}
			// else remove this digit from the set if it is there
			else solutions.remove(value);
		}
	}
		
	// use recursive backtracking at each spot 
	private boolean solveRecurse(LinkedList<Spot> spots, int index) {
		// dont continue recursion if numsolutions has maxed out
		if (numSolutions == Sudoku.MAX_SOLUTIONS) return true;
		// if there are no more spots to look at then the sudoku has been solved, so increment numSolutions, save the FIRST solution, then continue the recursion till it fails
		if (index == spots.size()) {
			numSolutions++;
			saveSolved();
			return false;
		}
		Spot spot = spots.get(index);
		// reset the solutions of the current spot
		spotSolutions(spot);
		Iterator<Integer> itr = spot.solutions.iterator();
		while (itr.hasNext()) {
			int value = itr.next();
			
			spot.set(value);
			if (solveRecurse(spots, index + 1)) return true;
			spot.unset(value);		
		}
		return false;		
	}
	
	public int solve() {		
		numSolutions = 0;
		startTime = System.currentTimeMillis(); 
		for (Spot spot : spots) {
			spotSolutions(spot);
		}
		spotSort(spots);
		solveRecurse(spots, 0);
		endTime = System.currentTimeMillis(); 
		return numSolutions; // YOUR CODE HERE
	}
	
	public String getSolutionText() {
		StringBuilder buff = new StringBuilder();
		if (numSolutions > 0) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE-1; j++) {
					buff.append(finalGrid[i][j]);
					buff.append(" ");
				}
				buff.append(finalGrid[i][SIZE-1]);
				buff.append("\n");
			}
		}
		return (buff.toString()); // YOUR CODE HERE
	}
	
	public long getElapsed() {
		return (endTime - startTime); // YOUR CODE HERE
	}
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid1 = Sudoku.stringsToGrid(
	"0 3 5 2 9 0 8 6 4",
	"0 8 2 4 1 0 7 0 3",
	"7 6 4 3 8 0 0 9 0",
	"2 1 8 7 3 9 0 4 0",
	"0 0 0 8 0 4 2 3 0",
	"0 4 3 0 5 2 9 7 0",
	"4 0 6 5 7 1 0 0 9",
	"3 5 9 0 2 8 4 1 7",
	"8 0 0 9 0 0 5 2 6");
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "5 3 0 0 7 0 0 0 0",
	 "6 0 0 1 9 5 0 0 0",
	 "0 9 8 0 0 0 0 6 0",
	 "8 0 0 0 6 0 0 0 3",
	 "4 0 0 8 0 3 0 0 1",
	 "7 0 0 0 2 0 0 0 6",
	 "0 6 0 0 0 0 2 8 0",
	 "0 0 0 4 1 9 0 0 5",
	 "0 0 0 0 8 0 0 7 9");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	public static final int[][] hardGrid1 = Sudoku.stringsToGrid(
	"3 0 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	public static final int[][] hardGrid2 = Sudoku.stringsToGrid(
	"3 0 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 0 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 0 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	public static final int[][] emptyGrid = Sudoku.stringsToGrid(
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0",
			"0 0 0 0 0 0 0 0 0");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}
	
	public String toString() {
		StringBuilder buff = new StringBuilder();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE-1; j++) {
				buff.append(grid[i][j]);
				buff.append(" ");
			}
			buff.append(grid[i][SIZE-1]);
			buff.append("\n");
		}
		return (buff.toString());
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
}
