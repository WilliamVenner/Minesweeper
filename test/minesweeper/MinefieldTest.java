package minesweeper;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MinefieldTest {
	private static Minefield emptyMinefield;
	private static Minefield minefield;
	private static Minefield fullMinefield;
	
	@Before
	public void setUp() {
		// Create three minefields for every test to use
		emptyMinefield = new Minefield(10, 10, 1);
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
		// Test that the minefields exist
		assertNotNull(minefield);
		assertNotNull(fullMinefield);
		assertNotNull(new Minefield(10, 10, 50));
	}
	
	@Test
	public void testMinefieldArrays() {
		// Test the minefield array lengths
		assertEquals(10, minefield.tiles.length);
		assertEquals(10, minefield.tiles[0].length);
	}
	
	@Test
	public void testMinefieldAttributes() {
		// Test some attributes of the minefield
		assertEquals(10, minefield.getColumnCount());
		assertEquals(10, minefield.getRowCount());
		assertEquals(50, minefield.getMaxMines());
		assertEquals(10 * 10, minefield.getTileCount());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldZeroRows() {
		// Test that zero rows throws an exception
		new Minefield(0, 10, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldZeroColumns() {
		// Test that zero columns throws an exception
		new Minefield(10, 0, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldOneTile() {
		// Test that a 1x1 minefield + 1 mine throws an exception (this is illegal)
		new Minefield(1, 1, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeRows() {
		// Test that negative rows throws an exception
		new Minefield(-1, 10, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeColumns() {
		// Test that negative mines throws an exception
		new Minefield(10, -1, 50);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldTooManyMines() {
		// 100 tiles = illegal because (0,0) should never have a mine
		new Minefield(10, 10, 100);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMinefieldNegativeMines() {
		// Test that negative mines throws an exception
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
		// Test that placing a mine at an out of bounds row throws an exception
		minefield.mineTile(minefield.getRowCount(), 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMineTileBoundsColumn() {
		// Test that placing a mine at an out of bounds column throws an exception
		minefield.mineTile(1, minefield.getColumnCount());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMineTile00() {
		// Test that placing a mine at (0,0) throws an exception
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
		assertEquals("  0123456789\n0 2*********\n1 *43333335*\n2 *3      3*\n3 *3      3*\n4 *3      3*\n5 *3      3*\n6 *3      3*\n7 *3      3*\n8 *53333335*\n9 **********", minefieldString);
	}
	
	@Test
	public void testMarkTile() {
		// Test marking tile
		minefield.markTile(5, 5);
		assertTrue(minefield.tiles[5][5].isMarked());
		
		minefield.markTile(5, 5);
		assertFalse(minefield.tiles[5][5].isMarked());
	}
	
	@Test
	public void testToStringMarkTile() {
		// Test marking tile toString()
		minefield.markTile(5, 5);
		
		assertEquals("  0123456789\n0 ##########\n1 ##########\n2 ##########\n3 ##########\n4 ##########\n5 #####!####\n6 ##########\n7 ##########\n8 ##########\n9 ##########", minefield.toString());
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
		assertEquals("  0123456789\n0 ##########\n1 #43333335#\n2 #3      3#\n3 #3      3#\n4 #3      3#\n5 #3      3#\n6 #3      3#\n7 #3      3#\n8 #53333335#\n9 ##########", minefieldString);
	}
	
	@Test
	public void testToStringGridCoordinates() {
		// Test the edge coordinates of our grid
		// We use 101x101 because 100 (101 - 1) has 3 characters, and isn't a palindrome, so
		// we can robustly test the edge coordinates of toString().
		Minefield bigMinefield = new Minefield(101, 101, (101 * 101 - 1));
		
		placeEdgeMines(bigMinefield);
		
		bigMinefield.step(2, 2);
		
		// Get the result of minefield.toString() and store it
		String minefieldString = bigMinefield.toString(true);
		
		// Compare to precalculated string
		assertEquals("                                                                                                        1\n              1111111111222222222233333333334444444444555555555566666666667777777777888888888899999999990\n    01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\n  0 2****************************************************************************************************\n  1 *433333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333335*\n  2 *3                                                                                                 3*\n  3 *3                                                                                                 3*\n  4 *3                                                                                                 3*\n  5 *3                                                                                                 3*\n  6 *3                                                                                                 3*\n  7 *3                                                                                                 3*\n  8 *3                                                                                                 3*\n  9 *3                                                                                                 3*\n 10 *3                                                                                                 3*\n 11 *3                                                                                                 3*\n 12 *3                                                                                                 3*\n 13 *3                                                                                                 3*\n 14 *3                                                                                                 3*\n 15 *3                                                                                                 3*\n 16 *3                                                                                                 3*\n 17 *3                                                                                                 3*\n 18 *3                                                                                                 3*\n 19 *3                                                                                                 3*\n 20 *3                                                                                                 3*\n 21 *3                                                                                                 3*\n 22 *3                                                                                                 3*\n 23 *3                                                                                                 3*\n 24 *3                                                                                                 3*\n 25 *3                                                                                                 3*\n 26 *3                                                                                                 3*\n 27 *3                                                                                                 3*\n 28 *3                                                                                                 3*\n 29 *3                                                                                                 3*\n 30 *3                                                                                                 3*\n 31 *3                                                                                                 3*\n 32 *3                                                                                                 3*\n 33 *3                                                                                                 3*\n 34 *3                                                                                                 3*\n 35 *3                                                                                                 3*\n 36 *3                                                                                                 3*\n 37 *3                                                                                                 3*\n 38 *3                                                                                                 3*\n 39 *3                                                                                                 3*\n 40 *3                                                                                                 3*\n 41 *3                                                                                                 3*\n 42 *3                                                                                                 3*\n 43 *3                                                                                                 3*\n 44 *3                                                                                                 3*\n 45 *3                                                                                                 3*\n 46 *3                                                                                                 3*\n 47 *3                                                                                                 3*\n 48 *3                                                                                                 3*\n 49 *3                                                                                                 3*\n 50 *3                                                                                                 3*\n 51 *3                                                                                                 3*\n 52 *3                                                                                                 3*\n 53 *3                                                                                                 3*\n 54 *3                                                                                                 3*\n 55 *3                                                                                                 3*\n 56 *3                                                                                                 3*\n 57 *3                                                                                                 3*\n 58 *3                                                                                                 3*\n 59 *3                                                                                                 3*\n 60 *3                                                                                                 3*\n 61 *3                                                                                                 3*\n 62 *3                                                                                                 3*\n 63 *3                                                                                                 3*\n 64 *3                                                                                                 3*\n 65 *3                                                                                                 3*\n 66 *3                                                                                                 3*\n 67 *3                                                                                                 3*\n 68 *3                                                                                                 3*\n 69 *3                                                                                                 3*\n 70 *3                                                                                                 3*\n 71 *3                                                                                                 3*\n 72 *3                                                                                                 3*\n 73 *3                                                                                                 3*\n 74 *3                                                                                                 3*\n 75 *3                                                                                                 3*\n 76 *3                                                                                                 3*\n 77 *3                                                                                                 3*\n 78 *3                                                                                                 3*\n 79 *3                                                                                                 3*\n 80 *3                                                                                                 3*\n 81 *3                                                                                                 3*\n 82 *3                                                                                                 3*\n 83 *3                                                                                                 3*\n 84 *3                                                                                                 3*\n 85 *3                                                                                                 3*\n 86 *3                                                                                                 3*\n 87 *3                                                                                                 3*\n 88 *3                                                                                                 3*\n 89 *3                                                                                                 3*\n 90 *3                                                                                                 3*\n 91 *3                                                                                                 3*\n 92 *3                                                                                                 3*\n 93 *3                                                                                                 3*\n 94 *3                                                                                                 3*\n 95 *3                                                                                                 3*\n 96 *3                                                                                                 3*\n 97 *3                                                                                                 3*\n 98 *3                                                                                                 3*\n 99 *533333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333335*\n100 *****************************************************************************************************", minefieldString);
	}
	
	@Test
	public void testAreAllMinesRevealed() {
		// Test that areAllMinesRevealed returns true when everything has been revealed
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
		// Test that areAllMinesRevealed returns false when only non-mines have been revealed
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
		// Test that areAllMinesRevealed returns false when something has been revealed
		placeEdgeMines(minefield);
		
		minefield.markTile(1, 1);
		
		assertFalse(minefield.areAllMinesRevealed());
	}
	
	@Test
	public void nullTestAreAllMinesRevealedNone() {
		// Test that areAllMinesRevealed returns false when nothing has been revealed
		placeEdgeMines(minefield);
		
		assertFalse(minefield.areAllMinesRevealed());
	}
	
	@Test
	public void testFirstMoveCantLose() {
		/* The first move of Minesweeper cannot result in a loss, it should move the mine elsewhere
		   if a mine is found on the starting tile */
		assertEquals(0, emptyMinefield.getMineCount());
		assertTrue(emptyMinefield.mineTile(1, 1));
		assertTrue(emptyMinefield.step(1, 1));
		assertEquals(1, emptyMinefield.getMineCount());
	}
	
	@Test
	public void testFirstMoveLoseEdgeCase() {
		/* If our minesweeper grid is 100% full of mines (excluding (0,0), we have an edge case when
		   we make our first move - (which should be a loss), but the rules of Minesweeper state the
		   first losing move must move the mine elsewhere.
		   The problem is that a 100% full grid has no where to place a mine except (0,0). So let's make sure
		   that doesn't happen... */
		
		// Populate the full minefield
		fullMinefield.populate();
		
		// Check (0,0) has no mine
		assertFalse(fullMinefield.tiles[0][0].isMined());
		
		// Check (5,5) has a mine
		assertTrue(fullMinefield.tiles[5][5].isMined());
		
		// Check if we didn't lose the game by stepping on the mine at (1,1)
		assertTrue(fullMinefield.step(5,5));
		
		// Check (0,0) has no mine again
		assertFalse(fullMinefield.tiles[0][0].isMined());
		
		// Check (5,5) has no mine
		assertFalse(fullMinefield.tiles[5][5].isMined());
		
		// Check minefield is full, minus one mine
		assertEquals(98, fullMinefield.getMineCount());
		
		// Check mine neighbour count of east tile to (5,5) - (4,5)
		assertTrue(fullMinefield.tiles[4][5].isMined());
		assertEquals(7, fullMinefield.tiles[4][5].getMineNeighbours());
		
		// Check toString() with precalculated string to do further neighbour calculation check
		assertEquals("  0123456789\n0 3*********\n1 **********\n2 **********\n3 **********\n4 **********\n5 *****8****\n6 **********\n7 **********\n8 **********\n9 **********", fullMinefield.toString(true));
	}
}
