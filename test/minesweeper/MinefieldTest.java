package minesweeper;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

// This will ensure that the tests are performed in order of top to bottom,
// preventing race conditions.
@FixMethodOrder(MethodSorters.JVM)
public class MinefieldTest {
	private static Minefield minefield;
	private static Minefield emptyMinefield;
	private static Minefield fullMinefield;
	
	@BeforeClass
	public static void setUpClass() {
		minefield = new Minefield(10, 10, 50);
		emptyMinefield = new Minefield(10, 10, 50);
		fullMinefield = new Minefield(10, 10, 99);
	}
	
	@Test
	public void testMinefieldExistence() {
		assertNotEquals(new Minefield(10, 10, 50), null);
	}
	
	@Test
	public void testMinefieldArrays() {
		assertEquals(minefield.mines.length, 10);
		assertEquals(minefield.mines[0].length, 10);
		assertEquals(minefield.mineNeighbours.length, 10);
		assertEquals(minefield.mineNeighbours[0].length, 10);
	}
	
	@Test
	public void testMinefieldAttributes() {
		assertEquals(minefield.getColumnCount(), 10);
		assertEquals(minefield.getRowCount(), 10);
		assertEquals(minefield.getMaxMines(), 50);
		assertEquals(minefield.getTileCount(), 10 * 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldZeroRows() {
		new Minefield(0, 10, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldZeroColumns() {
		new Minefield(10, 0, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldOneTile() {
		new Minefield(1, 1, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeRows() {
		new Minefield(-1, 10, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeColumns() {
		new Minefield(10, -1, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldTooManyMines() {
		// 100 mines = illegal because (0,0) should never have a mine
		new Minefield(10, 10, 100);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeMines() {
		new Minefield(10, 10, -1);
	}
	
	@Test
	public void testPopulate() {
		// Populate the minefield
		minefield.populate();
		
		// Count the number of mines on the minefield
		int mineCount = 0;
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int column = 0; column < minefield.getColumnCount(); column++) {
				if (minefield.mines[row][column]) {
					mineCount++;
				}
			}
		}
		assertEquals(mineCount, minefield.getMaxMines());
		
		// Check that (0,0) has no mine on it
		assertEquals(minefield.mines[0][0], false);
	}
	
	@Test
	public void testFullMinefieldPopulate() {
		// Populate the minefield
		fullMinefield.populate();
		
		// Count the number of mines on the minefield
		int mineCount = 0;
		for (int row = 0; row < fullMinefield.getRowCount(); row++) {
			for (int column = 0; column < fullMinefield.getColumnCount(); column++) {
				if (fullMinefield.mines[row][column]) {
					mineCount++;
				}
			}
		}
		assertEquals(mineCount, 99);
		
		// Check that (0,0) has no mine on it
		assertEquals(fullMinefield.mines[0][0], false);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMineTileBoundsRow() {
		minefield.mineTile(minefield.getRowCount(), 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMineTileBoundsColumn() {
		minefield.mineTile(1, minefield.getColumnCount());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMineTile00() {
		minefield.mineTile(0, 0);
	}
	
	@Test
	public void testMineTile() {
		assertEquals(emptyMinefield.getMineCount(), 0);
		
		// Place a mine at (1,1) on our empty minefield
		emptyMinefield.mineTile(1, 1);
		
		// Iterate through the row above, the mine's row, and the row below
		for (int row = 0; row <= 2; row++) {
			// Iterate through the column to the left, the mine's column, and the column to the right
			for (int col = 0; col <= 2; col++) {
				if (row == 1 && col == 1) {
					// Check if the mine was placed
					assertEquals(emptyMinefield.mines[row][col], true);
				} else {
					// Check if the neighbouring tile has 1 mine neighbour
					assertEquals(emptyMinefield.mineNeighbours[row][col], 1);
				}
			}
		}
		
		// Do the test again, to check if the neighbouring mine incrementation behaviour works
		// Place a mine next to the previous one
		emptyMinefield.mineTile(1, 2);
		
		// Iterate through the row above, the mine's row, and the row below
		for (int row = 0; row <= 2; row++) {
			// Iterate through the column to the left and the mine's column
			for (int col = 1; col <= 2; col++) {
				if (row == 1 && col == 2) {
					// Check if the mine was placed
					assertEquals(emptyMinefield.mines[row][col], true);
				} else if (row == 1 && col == 1) {
					// Check if the previous mine was placed
					assertEquals(emptyMinefield.mines[row][col], true);
				} else {
					// Check if the neighbouring tile has 2 mine neighbours
					assertEquals(emptyMinefield.mineNeighbours[row][col], 2);
				}
			}
			// Check if the right-neighbouring tile has 1 mine neighbour
			assertEquals(emptyMinefield.mineNeighbours[row][3], 1);
		}
		
		assertEquals(emptyMinefield.getMineCount(), 2);
	}
	
	@Test
	public void testToString() {
		// Enforce that we've placed a mine at all applicable corners of the minefield to robustly
		// test neighbouring mine calculations.
		minefield.mineTile(0, minefield.getColumnCount() - 1); // Top-right
		minefield.mineTile(minefield.getRowCount() - 1, minefield.getColumnCount() - 1); // Bottom-right
		minefield.mineTile(minefield.getRowCount() - 1, 0); // Bottom-left
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString();
		
		// Iterate over every tile on the minefield
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				if (row == 0 && col == 0) {
					// Make sure (0,0) has no mine placed
					assertEquals(minefield.mines[row][col], false);
				} else {
					// For every other tile, calculate its offset in the minefield string
					int tileStrCharPos = col + (row * (minefield.getColumnCount() + 1));
					String tileStrChar = String.valueOf(minefieldString.charAt(tileStrCharPos));
					if (minefield.mines[row][col]) {
						// Check whether this tile in the string is marked as a mine (asterisk)
						assertEquals(tileStrChar, "*");
					} else {
						// Calculate the number of neighbour mines for this tile
						int mineNeighbours = 0;
						
						// Neighbour tile calculation code copied from Minefield class:
						int rowsRangeMin = Math.max(row - 1, 0);
						int rowsRangeMax = Math.min(row + 1, minefield.getRowCount() - 1);
						for (int neighbourRow = rowsRangeMin; neighbourRow <= rowsRangeMax; neighbourRow++) {
							int columnsRangeMin = Math.max(col - 1, 0);
							int columnsRangeMax = Math.min(col + 1, minefield.getColumnCount() - 1);
							for (int neighbourCol = columnsRangeMin; neighbourCol <= columnsRangeMax; neighbourCol++) {
								// If this tile has a mine placed on it...
								if (minefield.mines[neighbourRow][neighbourCol]) {
									// Increment the number of neighbouring mines for this tile
									mineNeighbours++;
								}
							}
						}
						
						// Check whether the number of calculated neighbours matches the number returned in the minefield string
						assertEquals(tileStrChar, String.valueOf(mineNeighbours));
					}
				}
			}
		}
	}
}
