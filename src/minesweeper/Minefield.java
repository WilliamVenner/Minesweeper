package minesweeper;

import java.security.SecureRandom;

class Minefield {
	private final int rows;
	private final int columns;
	private final int tiles;
	private final int maxMines;
	private int mineCount = 0;

	public int getRowCount() {
		return rows;
	}

	public int getColumnCount() {
		return columns;
	}

	public int getTileCount() {
		return tiles;
	}

	public int getMaxMines() {
		return maxMines;
	}

	public int getMineCount() {
		return mineCount;
	}

	final boolean[][] mines;
	final int[][] mineNeighbours;

	public Minefield(int rows, int columns, int maxMines) {
		// Argument sanity checks for generating a legal minefield
		if (rows <= 0)
			throw new IllegalArgumentException("Must have 1 or more rows");
		if (columns <= 0)
			throw new IllegalArgumentException("Must have 1 or more columns");
		if (rows * columns <= 1)
			throw new IllegalArgumentException("Must be more than 1 tile");
		if (maxMines < 0)
			throw new IllegalArgumentException("Mines cannot be negative");
		if (maxMines > (rows * columns) - 1)
			throw new IllegalArgumentException("More mines than tiles available (excluding (0,0))");
		
		// Assign our attributes from the constructor arguments
		this.rows = rows;
		this.columns = columns;
		this.tiles = rows * columns;
		this.maxMines = maxMines;
		
		// Initialize mines and mineNeighbours with specified rows and columns
		mines = new boolean[rows][columns];
		mineNeighbours = new int[rows][columns];
	}

	public boolean mineTile(int row, int column) {
		// Argument sanity checks for coordinate boundaries and excepting (0,0)
		if (row < 0 || row >= rows)
			throw new IllegalArgumentException("Row coordinate out of range");
		if (column < 0 || column >= columns)
			throw new IllegalArgumentException("Column coordinate out of range");
		if (column == 0 && row == 0)
			throw new IllegalArgumentException("You cannot place a mine at (0,0)");
		
		/* If there's already a mine here, or we've already reached the maximum
		   number of mines on the minefield, return false */
		if (mines[row][column] || mineCount >= maxMines) {
			return false;
		} else {
			// Otherwise, place our mine at the provided coordinates
			mines[row][column] = true;
			// Increment mineCount
			mineCount++;
			
			// Precalculate the range of rows that need mine neighbour incrementations:
			int rowsRangeMin = Math.max(row - 1, 0); // The row above; otherwise, the row of the specified tile
			int rowsRangeMax = Math.min(row + 1, rows - 1); // The row below; otherwise, the row of the specified tile
			// This will iterate through the row above (if present), the specified tile's row, and the row below (if present)
			for (int neighbourRow = rowsRangeMin; neighbourRow <= rowsRangeMax; neighbourRow++) {
				// Precalculate the range of columns that need mine neighbour incrementations:
				int columnsRangeMin = Math.max(column - 1, 0); // The column to the left; otherwise, the column of the specified tile
				int columnsRangeMax = Math.min(column + 1, columns - 1); // The column to the right; otherwise, the column of the specified tile
				// This will iterate through the column to the left (if present), the specified tile's column, and the column to the right (if present)
				for (int neighbourCol = columnsRangeMin; neighbourCol <= columnsRangeMax; neighbourCol++) {
					// Increment the number of neighbouring mines for this neighbouring tile
					mineNeighbours[neighbourRow][neighbourCol] += 1;
				}
			}
			
			// Mine placement successful, return true
			return true;
		}
	}
	
	private void populateBruteforce(SecureRandom r) {
		// Attempt to place mines at random tiles until we've placed the amount required (maxMines)
		while (mineCount < maxMines) {
			/* Generates a random integer from 1..(tiles - 1) = (1,0)..(tiles-1,tiles-1)
			   This represents a 1-dimensional tile coordinate that we can convert to 2D */
			int coord = r.nextInt(tiles - 1) + 1;

			// Calculate row number from 1D tile coordinate
			int row = (int) Math.floor(coord / rows);

			// Calculate column number from 1D tile coordinate
			int column = coord % rows;

			// Attempt to place a mine at this tile
			mineTile(row, column);
		}
	}
	
	private void populateFisherYates(SecureRandom r) {
		// Initialize a boolean array which represents the minefield in 1D, excluding (0,0)
		boolean[] randomMines = new boolean[tiles - 1];
		// Populate the array with specified number of mines ("true")
		for (int i = 0; i < maxMines; i++) {
			randomMines[i] = true;
		}
		
		/* Perform the Sattolo variant of the Fisher-Yates shuffle
		   https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#Sattolo%27s_algorithm
		   https://danluu.com/sattolo/ */
		int n = randomMines.length;
		for (int i = 0; i < n - 1; i++) {
			int max = n - 1;
			int min = i + 1;
			
			// Generate random integer from [min..max]
			int j = r.nextInt((max - min) + 1) + min;
			
			// Swap array[i] and array[j] values
			boolean swap = randomMines[i];
			randomMines[i] = randomMines[j];
			randomMines[j] = swap;
		}
		
		// Populate 2D mines array using randomized 1D random mines array
		for (int i = 0; i < randomMines.length; i++) {
			if (randomMines[i]) {
				int coord = i + 1;
				
				// Calculate row number from 1D tile coordinate
				int row = (int) Math.floor(coord / rows);

				// Calculate column number from 1D tile coordinate
				int column = coord % rows;

				// Place a mine at this tile
				mineTile(row, column);
			}
		}
	}

	public void populate() {
		/* Use java.security.SecureRandom instead of java.util.Random because
		   values generated by java.util.Random can be predetermined and are
		   not cryptographically secure. */
		SecureRandom r = new SecureRandom();
		
		/* The Fisher-Yates array shuffling algorithm is significantly faster
		   than the bruteforcing method when the percentage of mines is >= 60%.
		   https://plot.ly/~WilliamVenner/2/ */
		if ((float)maxMines / (float)tiles >= 0.6) {
			populateFisherYates(r);
		} else {
			populateBruteforce(r);
		}
	}
	
	@Override
	public String toString() {
		// Use a StringBuilder here to efficiently allocate memory for repetitive appending
		StringBuilder minefieldStr = new StringBuilder();
		
		// Iterate over every row
		for (int row = 0; row < rows; row++) {
			// Iterate over every column in this row
			for (int column = 0; column < columns; column++) {
				if (mines[row][column]) {
					// If there is a mine on this tile, append an asterisk
					minefieldStr.append('*');
				} else {
					// Otherwise, append the number of neighbours of this tile that have mines
					minefieldStr.append(mineNeighbours[row][column]);
				}
			}
			// Append a newline to show a new row
			minefieldStr.append('\n');
		}
		
		// Finally, convert the StringBuilder to a string and return it
		return minefieldStr.toString();
	}
}
