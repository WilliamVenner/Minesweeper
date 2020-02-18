package minesweeper;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MinefieldTest {
	private static Minefield minefield;
	private static Minefield fullMinefield;
	
	@Before
	public void setUp() {
		minefield = new Minefield(10, 10, 50);
		fullMinefield = new Minefield(10, 10, 99);
	}
	
	@Test
	public void testMinefieldExistence() {
		assertNotNull(minefield);
		assertNotNull(fullMinefield);
		assertNotNull(new Minefield(10, 10, 50));
	}
	
	@Test
	public void testMinefieldArrays() {
		assertEquals(10, minefield.mines.length);
		assertEquals(10, minefield.mines[0].length);
		assertEquals(10, minefield.mineNeighbours.length);
		assertEquals(10, minefield.mineNeighbours[0].length);
	}
	
	@Test
	public void testMinefieldAttributes() {
		assertEquals(10, minefield.getColumnCount());
		assertEquals(10, minefield.getRowCount());
		assertEquals(50, minefield.getMaxMines());
		assertEquals(10 * 10, minefield.getTileCount());
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
		assertEquals(minefield.getMaxMines(), mineCount);
		
		// Check that (0,0) has no mine on it
		assertFalse(minefield.mines[0][0]);
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
		assertEquals(99, mineCount);
		
		// Check that (0,0) has no mine on it
		assertFalse(fullMinefield.mines[0][0]);
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
		assertEquals(0, minefield.getMineCount());
		
		// Place a mine at (1,1) on our empty minefield
		minefield.mineTile(1, 1);
		
		// Iterate through the row above, the mine's row, and the row below
		for (int row = 0; row <= 2; row++) {
			// Iterate through the column to the left, the mine's column, and the column to the right
			for (int col = 0; col <= 2; col++) {
				if (row == 1 && col == 1) {
					// Check if the mine was placed
					assertTrue(minefield.mines[row][col]);
				} else {
					// Check if the neighbouring tile has 1 mine neighbour
					assertEquals(1, minefield.mineNeighbours[row][col]);
				}
			}
		}
		
		// Do the test again, to check if the neighbouring mine incrementation behaviour works
		// Place a mine next to the previous one
		minefield.mineTile(1, 2);
		
		// Iterate through the row above, the mine's row, and the row below
		for (int row = 0; row <= 2; row++) {
			// Iterate through the column to the left and the mine's column
			for (int col = 1; col <= 2; col++) {
				if (row == 1 && col == 2) {
					// Check if the mine was placed
					assertTrue(minefield.mines[row][col]);
				} else if (row == 1 && col == 1) {
					// Check if the previous mine was placed
					assertTrue(minefield.mines[row][col]);
				} else {
					// Check if the neighbouring tile has 2 mine neighbours
					assertEquals(2, minefield.mineNeighbours[row][col]);
				}
			}
			// Check if the right-neighbouring tile has 1 mine neighbour
			assertEquals(1, minefield.mineNeighbours[row][3]);
		}
		
		assertEquals(2, minefield.getMineCount());
	}
	
	@Test
	public void testToStringPrecalculated() {
		/* Place mines around the edges of the grid (excluding (0,0))
		   This means we can easily compare against a precalculated string, as the
		   output will always be the same. */
		for (int row = 0; row < minefield.getRowCount(); row++) {
			if (row == 0 || row == minefield.getRowCount() - 1) {
				// If we're at the top or bottom edge...
				for (int col = 0; col < minefield.getColumnCount(); col++) {
					if (row != 0 || col != 0) { // Don't place a mine at (0,0)
						minefield.mineTile(row, col);
					}
				}
			} else {
				// Otherwise, only place mines on the sides
				for (int col = 0; col < 2; col++) {
					minefield.mineTile(row, col * (minefield.getColumnCount() - 1));
				}
			}
		}
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString();
		
		// Compare to precalculated string
		assertEquals("2*********\n*43333335*\n*30000003*\n*30000003*\n*30000003*\n*30000003*\n*30000003*\n*30000003*\n*53333335*\n**********", minefieldString);
	}
	
	@Test
	public void testToStringRandom() {
		// Generate a random minefield
		minefield.populate();
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString();
		
		// Iterate over every tile on the minefield and calculate the neighbouring mines
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				if (row == 0 && col == 0) {
					// Make sure (0,0) has no mine placed
					assertFalse(minefield.mines[row][col]);
				} else {
					// For every other tile, calculate its offset in the minefield string
					int tileStrCharPos = col + (row * (minefield.getColumnCount() + 1));
					String tileStrChar = String.valueOf(minefieldString.charAt(tileStrCharPos));
					if (minefield.mines[row][col]) {
						// Check whether this tile in the string is marked as a mine (asterisk)
						assertEquals("*", tileStrChar);
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
						assertEquals(String.valueOf(mineNeighbours), tileStrChar);
					}
				}
			}
		}
	}
}
