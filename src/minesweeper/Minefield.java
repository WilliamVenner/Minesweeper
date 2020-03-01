package minesweeper;

import java.security.SecureRandom;
import java.util.Arrays;

class Minefield {
	private final int rows;
	private final int columns;
	private final int tileCount;
	private final int maxMines;
	private int mineCount = 0;
	
	private boolean firstMove = true;
	
	private SecureRandom random;

	public int getRowCount() {
		return rows;
	}

	public int getColumnCount() {
		return columns;
	}

	public int getTileCount() {
		return tileCount;
	}

	public int getMaxMines() {
		return maxMines;
	}

	public int getMineCount() {
		return mineCount;
	}

	final MineTile[][] tiles;

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
		this.tileCount = rows * columns;
		this.maxMines = maxMines;
		
		// Initialize tiles with specified rows and columns
		tiles = new MineTile[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				tiles[row][col] = new MineTile();
			}
		}
	}
	
	public boolean areAllMinesRevealed() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				if (tiles[row][col].isMined() ^ tiles[row][col].isMarked()) {
					return false;
				}
			}
		}
		return true;
	}

	public void markTile(int row, int column) {
		// Argument sanity checks for coordinate boundaries
		if (row < 0 || row >= rows)
			throw new IllegalArgumentException("Row coordinate out of range");
		if (column < 0 || column >= columns)
			throw new IllegalArgumentException("Column coordinate out of range");
		
		// Toggle marked
		tiles[row][column].toggleMarked();
	}
	
	public boolean step(int row, int column) {
		// Argument sanity checks for coordinate boundaries
		if (row < 0 || row >= rows)
			throw new IllegalArgumentException("Row coordinate out of range");
		if (column < 0 || column >= columns)
			throw new IllegalArgumentException("Column coordinate out of range");

		boolean lostGame = tiles[row][column].isMined();
		if (lostGame) {
			if (firstMove) {
				// If this is the first move, move the mine somewhere else...
				tiles[row][column].setMined(false);
				// The Bruteforce method will find somewhere to place the mine for us
				populateBruteforce(random);
			} else {
				return false;
			}
		}
		
		firstMove = false;
		
		if (tiles[row][column].getMineNeighbours() == 0) {
			// If a tile has 0 neighbours, then it should be revealed, and all its neighbours should be searched
			// Precalculate the range of rows that need revealing:
			int rowsRangeMin = Math.max(row - 1, 0); // The row above; otherwise, the row of the specified tile
			int rowsRangeMax = Math.min(row + 1, rows - 1); // The row below; otherwise, the row of the specified tile
			// This will iterate through the row above (if present), the specified tile's row, and the row below (if present)
			for (int neighbourRow = rowsRangeMin; neighbourRow <= rowsRangeMax; neighbourRow++) {
				// Precalculate the range of columns that need revealing:
				int columnsRangeMin = Math.max(column - 1, 0); // The column to the left; otherwise, the column of the specified tile
				int columnsRangeMax = Math.min(column + 1, columns - 1); // The column to the right; otherwise, the column of the specified tile
				// This will iterate through the column to the left (if present), the specified tile's column, and the column to the right (if present)
				for (int neighbourCol = columnsRangeMin; neighbourCol <= columnsRangeMax; neighbourCol++) {
					if (!tiles[neighbourRow][neighbourCol].isRevealed()) {
						if (neighbourRow == row && neighbourCol == column) {
							// Reveal the specified tile
							tiles[row][column].reveal();
						} else {
							// Recursively reveal the neighbouring tile
							step(neighbourRow, neighbourCol);
						}
					}
				}
			}
		} else {
			// If a tile has 1 or more neighbours, then just that tile should be revealed
			tiles[row][column].reveal();
		}
		
		return true;
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
		   number of tiles on the minefield, return false */
		if (tiles[row][column].isMined() || mineCount >= maxMines) {
			return false;
		} else {
			// Otherwise, place our mine at the provided coordinates
			tiles[row][column].setMined(true);
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
					// Increment the number of neighbouring tiles for this neighbouring tile
					tiles[neighbourRow][neighbourCol].addMineNeighbour();
				}
			}
			
			// Mine placement successful, return true
			return true;
		}
	}
	
	private void populateBruteforce(SecureRandom r) {
		// Attempt to place tiles at random tiles until we've placed the amount required (maxMines)
		while (mineCount < maxMines) {
			/* Generates a random integer from 1..(tileCount - 1) = (1,0)..(tileCount-1,tileCount-1)
			   This represents a 1-dimensional tile coordinate that we can convert to 2D */
			int coord = r.nextInt(tileCount - 1) + 1;

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
		boolean[] randomMines = new boolean[tileCount - 1];
		// Populate the array with specified number of tiles ("true")
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
		
		// Populate 2D tiles array using randomized 1D random tiles array
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
		random = new SecureRandom();
		
		/* The Fisher-Yates array shuffling algorithm is significantly faster
		   than the bruteforcing method when the percentage of tiles is >= 60%.
		   https://plot.ly/~WilliamVenner/2/ */
		if ((float)maxMines / (float)tileCount >= 0.6) {
			populateFisherYates(random);
		} else {
			populateBruteforce(random);
		}
	}
	
	public String toString(boolean forceReveal) {
		// Use a StringBuilder here to efficiently allocate memory for repetitive appending
		StringBuilder minefieldStr = new StringBuilder();
		
		int rowSpaces   = String.valueOf(rows - 1).length() - 1;
		int columnLines = String.valueOf(columns - 1).length();
		
		if (columnLines > 1) {
			String[][] columnDigits = new String[columns][columnLines];
			for (int col = 0; col < columns; col++) {
				String colStr = String.valueOf(col);
				if (colStr.length() == columnLines) {
					columnDigits[col] = colStr.split("");
				} else {
					String[] colDigits = colStr.split("");
					int spacesNeeded = columnLines - colStr.length();
					for (int space = 0; space < columnLines; space++) {
						if (space >= spacesNeeded) {
							columnDigits[col][space] = colDigits[space - spacesNeeded];
						} else {
							columnDigits[col][space] = " ";
						}
					}
				}
			}
			
			for (int line = 0; line < columnLines; line++) {
				StringBuilder lineStr = new StringBuilder();
				for (int space = 0; space <= rowSpaces + 1; space++) {
					minefieldStr.append(" ");
				}
				for (int col = 0; col < columns; col++) {
					if (columnDigits[col].length - 1 >= line) {
						lineStr.append(columnDigits[col][line]);
					} else {
						lineStr.append(" ");
					}
				}
				minefieldStr.append(lineStr.toString() + "\n");
			}
		} else {
			for (int space = 0; space <= rowSpaces + 1; space++) {
				minefieldStr.append(" ");
			}
			for (int col = 0; col < columns; col++) {
				minefieldStr.append(col);
			}
			minefieldStr.append("\n");
		}
		
		// Iterate over every row
		for (int row = 0; row < rows; row++) {
			int rowNumLength = String.valueOf(row).length();
			for (int space = rowNumLength; space <= rowSpaces; space++) {
				minefieldStr.append(" ");
			}
			minefieldStr.append(row + " ");
			// Iterate over every column in this row
			for (int column = 0; column < columns; column++) {
				minefieldStr.append(tiles[row][column].toString(forceReveal));
			}
			if (row != rows - 1) {
				// Append a newline to show a new row
				minefieldStr.append('\n');
			}
		}
		
		// Finally, convert the StringBuilder to a string and return it
		return minefieldStr.toString();
	}
	
	@Override
	public String toString() {
		return this.toString(false);
	}
}
