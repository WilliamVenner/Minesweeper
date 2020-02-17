package minesweeper;

public class Minesweeper {
	private static Minefield minefield;

	public static void main(String[] args) {
		minefield = new Minefield(10, 10, 50);
	}
}
