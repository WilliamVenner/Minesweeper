package minesweeper;

public class Minesweeper {
	private static Minefield minefield;
	private static CommandLineListener commandLineListener;

	public static void main(String[] args) {
		commandLineListener = new CommandLineListener();
	}
	
	static Minefield newGame(int rows, int columns, int maxMines) {
		minefield = new Minefield(rows, columns, maxMines);
		minefield.populate();
		return minefield;
	}
}
