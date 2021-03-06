/**
 * Mark Van der Merwe and Tarun Sunkaraneni
 */
package Sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Sudoku class, that stores a sudoku puzzle as a 1D array and solves the puzzle
 * using both a recursive and constraint solver.
 * 
 * @author markvandermerwe and tarunsunkaraneni
 *
 */
public class Sudoku {

	// Integer array representing all the values in our puzzle.
	private int[] puzzle = new int[81];
	// Number of guesses for the recursive solver.
	private int guessCount = 0;
	// If verifying puzzle, alter validity tests slightly for reuse.
	private boolean isChecking;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public Sudoku(String fileName) throws Exception {
		File sudokuFile = new File("src/puzzles/" + fileName);
		try {
			Scanner scanner = new Scanner(sudokuFile);
			int index = 0;
			// Parse the puzzle file and place the values inside of our puzzle
			// array.
			while (scanner.hasNextInt()) {
				int s = scanner.nextInt();
				puzzle[index] = s;
				index++;
			}

			if (index > 81) {// because index increments one value after there's
								// 80 indices, so we have to make the inequality
								// less than 81
				throw new Exception();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " not found!");
			e.printStackTrace();
		}
	}

	/**
	 * Create a new puzzle by reading a file
	 *
	 * the file should be 9 rows of 9 numbers separated by whitespace
	 * 
	 *
	 */
	public Sudoku(BufferedReader reader) throws IOException {
		for (int i = 0; i < 9; i++) {
			int count = 0;
			while (count < 9) {
				int currentValue = Character.getNumericValue(reader.read());
				if (currentValue >= 0) {
					puzzle[count + i * 9] = currentValue;
					count++;
				}
			}
		}
	}

	/**
	 * @return a copy of the puzzle as a 2D matrix
	 */
	public int[][] get_puzzle() {
		int[][] puzzleMatrix = new int[9][9];
		// Cycle through the rows and columns of the 2D puzzle matrix.
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				// To get value, must add which column we're in to nine times
				// the row so that it lines up correctly.
				puzzleMatrix[row][column] = puzzle[(row * 9) + column];
			}
		}
		return puzzleMatrix;
	}

	/**
	 * @return how many guesses it took to recursively solve the problem.
	 */
	public int get_guess_count() {
		return this.guessCount;
	}

	/**
	 * Function: valid_for_row
	 *
	 * Description: Determine if the given number can be placed in the given row
	 * without violating the rules of sudoku
	 *
	 * Inputs:
	 * 
	 * @input row : which row to see if the number can go into
	 * @input number: the number of interest
	 *
	 *        Outputs:
	 *
	 *        true if it is possible to place that number in the row without
	 *        violating the rule of 1 unique number per row.
	 *
	 *        In other words, if the given number is already present in the row,
	 *        it is not possible to place it again (return false), otherwise it
	 *        is possible to place it (return true);
	 * 
	 *        WARNING: call this function before placing the number in the
	 *        puzzle...
	 *
	 */
	public boolean valid_for_row(int row, int number) {
		if (row > 8 || row < 0 || number < 1 || number > 9) {
			throw new IndexOutOfBoundsException();
		}

		int count = 0;

		int currentRow = 9 * row;
		for (int currentColumn = 1; currentColumn < 9; currentColumn++) {
			if (puzzle[currentRow + currentColumn] == number) {
				// System.out.println("false");
				count++;
			}
		}

		if (count == 1 && !isChecking) {
			return false;
		} else if (count == 1 && isChecking) {
			return true;
		} else {
			return true;
		}
	}

	/**
	 * Function: valid_for_col (see above)
	 */
	public boolean valid_for_column(int col, int number) {
		if (col > 8 || col < 0 || number < 1 || number > 9) {
			throw new IndexOutOfBoundsException();
		}

		int count = 0;

		for (int currentRow = 0; currentRow < 9; currentRow++) {
			if (puzzle[col + (9 * currentRow)] == number) {
				// System.out.println("false");
				count++;
			}
		}

		if (count == 1 && !isChecking) {
			return false;
		} else if (count == 1 && isChecking) {
			return true;
		} else {
			return true;
		}
	}

	/**
	 * Function: valid_for_box (see above)
	 *
	 * The sudoku boxes are:
	 *
	 * 0 | 1 | 2 ---+---+--- 3 | 4 | 5 ---+---+--- 6 | 7 | 8
	 *
	 * where each box represents a 3x3 square in the game.
	 *
	 */
	public boolean valid_for_box(int box, int number) {
		// Determine which row/col to start in based on box.
		int rowStart = 3 * (box / 3);
		int colStart = 3 * (box % 3);

		int count = 0;

		for (int row = rowStart; row < rowStart + 3; row++) {
			for (int col = colStart; col < colStart + 3; col++) {
				// For each value in box, test if it is the given number.
				if (this.puzzle[(9 * row) + col] == number) {
					// System.out.println("false");
					count++;
				}
			}
		}

		if (count == 1 && !isChecking) {
			return false;
		} else if (count == 1 && isChecking) {
			return true;
		} else {
			return true;
		}
	}

	/**
	 *
	 * Function is_valid( position, value )
	 *
	 * Determine if the given value is valid in the puzzle at that position.
	 *
	 * Inputs:
	 * 
	 * @param position
	 *            - which bucket in the puzzle to check for validity - should be
	 *            empty
	 * @param possible_value
	 *            - the value to check (1-9)
	 * 
	 * @return true if valid
	 */
	public boolean is_valid(int position, int possible_value) {
		if (possible_value == 0) {
			return false;
		}

		// Convert position to row, col, box for use in validation.
		int row = position / 9;
		int column = position % 9;
		int box = (3 * (row / 3)) + (column / 3);

		if (this.valid_for_row(row, possible_value) && this.valid_for_column(column, possible_value)
				&& this.valid_for_box(box, possible_value)) {
			return true;
		}
		return false;
	}

	/**
	 * solve the sudoku problem
	 * 
	 * @return true if successful
	 */
	public boolean solve_sudoku() {
		solve_sudoku(0);
		if (verify()) {
			// System.out.println("Puzzle solved.");
			// System.out.println(this.toString());
			// System.out.println("Puzzle solved in " + this.guessCount + "
			// guesses.");
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * Function solve_sudoku( puzzle, position )
	 *
	 * Recursively check to see if the puzzle can be solved following class
	 * algorithm
	 *
	 * Inputs: position - the index of the "current" box in the array (should be
	 * set to 0 by initial call)
	 *
	 */
	public boolean solve_sudoku(int position) {
		// If end of puzzle, return true (puzzle solved).
		if (position == 81) {
			return true;
		}

		int valueAtIndex = puzzle[position];

		// If not value present, then attempt to solve that position.
		if (valueAtIndex == 0) {
			// Iterate through the possible solutions for this position.
			for (int possibleSolution = 1; possibleSolution <= 9; possibleSolution++) {
				// Increment for guess.
				this.guessCount++;
				// Check if it is a valid solution.
				if (is_valid(position, possibleSolution)) {
					puzzle[position] = possibleSolution;

					// If true tell the next position to solve.
					if (solve_sudoku(position + 1)) {
						// If it returns true, return true to signal that the
						// current value set works.
						return true;
					}
				}
			}
			// If we reach here then no values work at that position and we need
			// to
			// change the previous position's value.
			puzzle[position] = 0;
			return false;
		} else {
			// If number is set before, we still need to be able to recurse
			// "through" it.
			return solve_sudoku(position + 1);
		}

	}

	/**
	 * Function: toString( )
	 *
	 * @return a string showing the state of the board
	 *
	 */
	@Override
	public String toString() {
		String puzzleString = "";
		// Get puzzle as matrix.
		int[][] puzzle = get_puzzle();

		// iterate through the puzzle and paste with space after each number and
		// new line after each row.
		for (int row = 0; row < 9; row++) {
			if (row % 3 == 0) {
				puzzleString += "---------------------\n";
			}
			for (int column = 0; column < 9; column++) {
				if (column % 3 == 0) {
					puzzleString += "|";
				}
				puzzleString += puzzle[row][column] + " ";
				if (column == 8) {
					puzzleString += "|";
				}
			}
			puzzleString += "\n";
		}
		puzzleString += "---------------------\n";
		return puzzleString;

	}

	/**
	 * Given a puzzle (filled or partial), verify that every element does not
	 * repeat in row, col, or box.
	 * 
	 * @return true if a validly solved puzzle
	 */
	public boolean verify() {
		// isChecking tells validation to act slightly differently.
		this.isChecking = true;
		// Go through whole puzzle and verify each value.
		for (int index = 0; index < 81; index++) {
			if (puzzle[index] == 0) {
				this.isChecking = false;
				return false;
			}

			// Check value at position.
			this.is_valid(index, puzzle[index]);

		}
		this.isChecking = false;
		return true;
	}

	/**
	 * Attempt to solve a sudoku by eliminating obviously wrong values Algorithm
	 *
	 * 1) build a 81 (representing 9x9) array of sets 2) put a set of 1-9 in
	 * each of the 81 spots 3) do initial elimination for each known value,
	 * eliminate nubmers from sets in same row, col, box eliminate all values in
	 * the given square 4) while progress is being made (initially true) find a
	 * non-processed square with one possible answer and eliminate this number
	 * from row, col, box
	 */
	public void solve_by_elimination() {
		// Each HashSet represents all possible values (1-9) for each position
		// in our puzzle.
		ArrayList<HashSet<Integer>> possibilities = new ArrayList<>();
		for (int index = 0; index < 81; index++) {
			// For each of the 81 spots in the puzzle, create a hashset of
			// possible values 1-9
			HashSet<Integer> possibleSet = new HashSet<>();
			for (int possibility = 1; possibility <= 9; possibility++) {
				possibleSet.add(possibility);
			}
			possibilities.add(index, possibleSet);
		}

		do {
			// Write solving code here.
			for (int index = 0; index < 81; index++) {
				if (puzzle[index] != 0) {
					possibilities.get(index).clear();
					prune_box(possibilities, index, puzzle[index]);
					prune_column(possibilities, index, puzzle[index]);
					prune_row(possibilities, index, puzzle[index]);
				} else {
					if (possibilities.get(index).size() == 1) {
						puzzle[index] = (int) possibilities.get(index).toArray()[0];
					}
				}
			}
			// Decides whether to continue based on vals in possibilities.
		} while (continueSolve(possibilities));
	}

	/**
	 * Helper method that decides if we try to solve another round with
	 * elimination. We decide by seeing if any of the HashSets are size one
	 * which implies a new location has been set.
	 * 
	 * Note: Because each box is also pruned of its last value, we don't have to
	 * worry about an infinite loop.
	 * 
	 * @param possibilities
	 *            - our list of HashSet possibilities
	 * @return - whether or not to solve another time w/ elimination
	 */
	protected boolean continueSolve(ArrayList<HashSet<Integer>> possibilities) {
		for (int index = 0; index < possibilities.size(); index++) {
			if (possibilities.get(index).size() == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * print a grid showing all possible valid answers This should be a 27x27
	 * matrix.
	 *
	 * I would suggest you do this first to get good debugging help
	 * 
	 * @param possibilities
	 *            - array list of all the sets of 1-9s
	 */
	protected static void print_possibilities(ArrayList<HashSet<Integer>> possibilities) {

		for (int index = 0; index < possibilities.size(); index++) {
			System.out.println(possibilities.get(index).toString());
		}

	}

	/**
	 * Given a possibility constraint matrix (81 sets of [1-9]) remove the given
	 * number from every matrix in the given box
	 * 
	 * @param possibilities
	 *            - 81 sets of [1-9]
	 * @param position
	 *            - where the value exists (transform to row,col)
	 * @param value
	 *            - the value to prune
	 */
	protected static void prune_box(ArrayList<HashSet<Integer>> possibilities, int position, Integer value) {
		int row = position / 9;
		int column = position % 9;
		int box = (3 * (row / 3)) + (column / 3);
		int rowStart = 3 * (box / 3);
		int colStart = 3 * (box % 3);
		for (int currentRow = rowStart; currentRow < rowStart + 3; currentRow++) {
			for (int currentCol = colStart; currentCol < colStart + 3; currentCol++) {
				possibilities.get((9 * currentRow) + currentCol).remove(value);
			}
		}
	}

	/**
	 * Given a possibility constraint matrix (81 sets of [1-9]) remove the given
	 * number from every matrix in the given column
	 * 
	 * @param possibilities
	 *            - 81 sets of [1-9]
	 * @param position
	 *            - where the value exists (transform to row,col)
	 * @param value
	 *            - the value to prune
	 */
	protected static void prune_column(ArrayList<HashSet<Integer>> possibilities, int position, Integer value) {
		int col = position % 9;
		for (int currentRow = 0; currentRow < 9; currentRow++) {
			possibilities.get(col + (9 * currentRow)).remove(value);
		}
	}

	/**
	 * Given a possibility constraint matrix (81 sets of [1-9]) remove the given
	 * number from every matrix in the given row
	 * 
	 * @param possibilities
	 *            - 81 sets of [1-9]
	 * @param position
	 *            - where the value exists (transform to row,col)
	 * @param value
	 *            - the value to prune
	 */
	protected static void prune_row(ArrayList<HashSet<Integer>> possibilities, int position, Integer value) {
		int row = position / 9;
		for (int currentColumn = 0; currentColumn < 9; currentColumn++) {
			possibilities.get((row * 9) + currentColumn).remove(value);
		}
	}

	/**
	 * Determines what percent of the current puzzle object is complete.
	 * 
	 * @return percent complete of current puzzle.
	 */
	public double percentComplete() {
		int complete = 0;
		for (int index = 0; index < puzzle.length; index++) {
			if (puzzle[index] != 0) {
				complete++;
			}
		}
		// Divide the numbers in puzzle by total size for percentage.
		return (double) complete / 81;
	}

}
