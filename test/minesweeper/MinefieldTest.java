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
	
	private static void placeEdgeMines(Minefield minefield) {
		/* Place tiles around the edges of the grid (excluding (0,0))
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
				// Otherwise, only place tiles on the sides
				for (int col = 0; col < 2; col++) {
					minefield.mineTile(row, col * (minefield.getColumnCount() - 1));
				}
			}
		}
	}
	
	@Test
	public void testMinefieldExistence() {
		assertNotNull(new Minefield(10, 10, 50));
	}
	
	@Test
	public void testMinefieldArrays() {
		assertEquals(10, minefield.tiles.length);
		assertEquals(10, minefield.tiles[0].length);
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
		// 100 tiles = illegal because (0,0) should never have a mine
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
		
		// Count the number of tiles on the minefield
		int mineCount = 0;
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int column = 0; column < minefield.getColumnCount(); column++) {
				if (minefield.tiles[row][column].isMined()) {
					mineCount++;
				}
			}
		}
		assertEquals(minefield.getMaxMines(), mineCount);
		
		// Check that (0,0) has no mine on it
		assertFalse(minefield.tiles[0][0].isMined());
	}
	
	@Test
	public void testFullMinefieldPopulate() {
		// Populate the minefield
		fullMinefield.populate();
		
		// Count the number of tiles on the minefield
		int mineCount = 0;
		for (int row = 0; row < fullMinefield.getRowCount(); row++) {
			for (int column = 0; column < fullMinefield.getColumnCount(); column++) {
				if (fullMinefield.tiles[row][column].isMined()) {
					mineCount++;
				}
			}
		}
		assertEquals(99, mineCount);
		
		// Check that (0,0) has no mine on it
		assertEquals(fullMinefield.tiles[0][0].isMined(), false);
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
					assertTrue(minefield.tiles[row][col].isMined());
				} else {
					// Check if the neighbouring tile has 1 mine neighbour
					assertEquals(1, minefield.tiles[row][col].getMineNeighbours());
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
					assertTrue(minefield.tiles[row][col].isMined());
				} else if (row == 1 && col == 1) {
					// Check if the previous mine was placed
					assertTrue(minefield.tiles[row][col].isMined());
				} else {
					// Check if the neighbouring tile has 2 mine neighbours
					assertEquals(2, minefield.tiles[row][col].getMineNeighbours());
				}
			}
			// Check if the right-neighbouring tile has 1 mine neighbour
			assertEquals(1, minefield.tiles[row][3].getMineNeighbours());
		}
		
		assertEquals(2, minefield.getMineCount());
	}
	
	@Test
	public void testToStringPrecalculated() {
		placeEdgeMines(minefield);
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString(true);
		
		// Compare to precalculated string
		assertEquals("2*********\n*43333335*\n*3      3*\n*3      3*\n*3      3*\n*3      3*\n*3      3*\n*3      3*\n*53333335*\n**********", minefieldString);
	}
	
	@Test
	public void testToStringRandom() {
		// Generate a random minefield
		minefield.populate();
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString(true);
		
		// Iterate over every tile on the minefield and calculate the neighbouring tiles
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				if (row == 0 && col == 0) {
					// Make sure (0,0) has no mine placed
					assertFalse(minefield.tiles[row][col].isMined());
				} else {
					// For every other tile, calculate its offset in the minefield string
					int tileStrCharPos = col + (row * (minefield.getColumnCount() + 1));
					String tileStrChar = String.valueOf(minefieldString.charAt(tileStrCharPos));
					if (minefield.tiles[row][col].isMined()) {
						// Check whether this tile in the string is marked as a mine (asterisk)
						assertEquals("*", tileStrChar);
					} else {
						// Calculate the number of neighbour tiles for this tile
						int mineNeighbours = 0;

						// Neighbour tile calculation code copied from Minefield class:
						int rowsRangeMin = Math.max(row - 1, 0);
						int rowsRangeMax = Math.min(row + 1, minefield.getRowCount() - 1);
						for (int neighbourRow = rowsRangeMin; neighbourRow <= rowsRangeMax; neighbourRow++) {
							int columnsRangeMin = Math.max(col - 1, 0);
							int columnsRangeMax = Math.min(col + 1, minefield.getColumnCount() - 1);
							for (int neighbourCol = columnsRangeMin; neighbourCol <= columnsRangeMax; neighbourCol++) {
								// If this tile has a mine placed on it...
								if (minefield.tiles[neighbourRow][neighbourCol].isMined()) {
									// Increment the number of neighbouring tiles for this tile
									mineNeighbours++;
								}
							}
						}
						
						if (mineNeighbours == 0) {
							// Check whether this tile with 0 mine neighbours is shown as a space
							assertEquals(" ", tileStrChar);
						} else {
							// Check whether the number of calculated neighbours matches the number returned in the minefield string
							assertEquals(String.valueOf(mineNeighbours), tileStrChar);
						}
					}
				}
			}
		}
	}
	
	@Test
	public void testMarkTile() {
		minefield.markTile(5, 5);
		assertTrue(minefield.tiles[5][5].isMarked());
		
		minefield.markTile(5, 5);
		assertFalse(minefield.tiles[5][5].isMarked());
	}
	
	@Test
	public void testToStringMarkTile() {
		minefield.markTile(5, 5);
		assertEquals("##########\n##########\n##########\n##########\n##########\n#####!####\n##########\n##########\n##########\n##########", minefield.toString());
	}
	
	@Test
	public void testReveal() {
		placeEdgeMines(minefield);
		
		// Reveal (2,2), this should recursively reveal everything around it as well.
		// Because we placed mines around the edges, we know which tiles will be recursively revealed,
		// so we can compare against a precalculated string.
		minefield.step(2, 2);
		
		// Get the result of minefield.toString() and store it
		String minefieldString = minefield.toString();
		
		// Compare to precalculated string
		assertEquals("##########\n#43333335#\n#3      3#\n#3      3#\n#3      3#\n#3      3#\n#3      3#\n#3      3#\n#53333335#\n##########", minefieldString);
	}
	
	@Test
	public void testAreAllMinesRevealed() {
		placeEdgeMines(minefield);
		
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				if (minefield.tiles[row][col].isMined()) {
					minefield.markTile(row, col);
				}
			}
		}
		
		assertTrue(minefield.areAllMinesRevealed());
	}
	
	@Test
	public void nullTestAreAllMinesRevealed() {
		placeEdgeMines(minefield);
		
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				if (!minefield.tiles[row][col].isMined()) {
					minefield.markTile(row, col);
				}
			}
		}
		
		assertFalse(minefield.areAllMinesRevealed());
	}
	
	@Test
	public void nullTestAreAllMinesRevealedSingle() {
		placeEdgeMines(minefield);
		
		minefield.markTile(1, 1);
		
		assertFalse(minefield.areAllMinesRevealed());
	}
	
	@Test
	public void nullTestAreAllMinesRevealedNone() {
		placeEdgeMines(minefield);
		
		assertFalse(minefield.areAllMinesRevealed());
	}
}
