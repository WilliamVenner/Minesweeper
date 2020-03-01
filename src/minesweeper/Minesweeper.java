package minesweeper;

public class Minesweeper {
	private static Minefield minefield;

	public static void main(String[] args) {
		new CommandLineListener();
	}
	
	public static Minefield newGame(int rows, int columns, int maxMines) {
		Minefield minefield = new Minefield(rows, columns, maxMines);
		minefield.populate();
		return minefield;
	}
}
