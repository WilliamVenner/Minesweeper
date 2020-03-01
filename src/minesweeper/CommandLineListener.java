package minesweeper;

import minesweeper.CommandLine.Command;
import minesweeper.CommandLine.CommandWord;
import minesweeper.CommandLine.Parser;

public class CommandLineListener {
	private Minefield minefield;
	
	Parser parser = new Parser();
	
	private boolean checkBounds(int row, int column) {
		boolean rowOutOfBounds = row >= minefield.getRowCount();
		boolean columnOutOfBounds = column >= minefield.getColumnCount();
		if (rowOutOfBounds && columnOutOfBounds) {
			System.out.println("Row and column out of bounds!");
			return false;
		} else if (rowOutOfBounds) {
			System.out.println("Row out of bounds!");
			return false;
		} else if (columnOutOfBounds) {
			System.out.println("Column out of bounds!");
			return false;
		}
		return true;
	}
	
	private boolean checkWin() {
		if (minefield.areAllMinesRevealed()) {
			System.out.println("You've revealed & marked all mines, and haven't incorrectly marked any tiles.");
			System.out.println("Congratulations - you win!");
			System.out.println(minefield.toString(true));
			minefield = null;
			return true;
		}
		return false;
	}
	
	private void execute(Command c) {
		if (c.getCommand() == CommandWord.UNKNOWN) {
			printPrompt(c.getMsg());
		} else if (c.getCommand() != CommandWord.NEW && minefield == null) {
			printPrompt("Please start a game first");
		} else {
			switch(c.getCommand()) {
				case NEW:
					int mineCount = Math.max((int)(c.getRow() * c.getColumn() * .1), 1);
					minefield = Minesweeper.newGame(c.getRow(), c.getColumn(), mineCount);
					System.out.println(minefield.toString());
					break;

				case STEP:
					if (checkBounds(c.getRow(), c.getColumn())) {
						if (!minefield.step(c.getRow(), c.getColumn())) {
							System.out.println("You stepped on a mine! GAME OVER");
							System.out.println(minefield.toString(true));
							minefield = null;
						} else if (!checkWin()) {
							System.out.println(minefield.toString());
						}
					}
					break;

				case MARK:
					if (checkBounds(c.getRow(), c.getColumn())) {
						minefield.markTile(c.getRow(), c.getColumn());
						if (!checkWin()) {
							System.out.println(minefield.toString());
						}
					}
					break;
			}
			printPrompt(c.getMsg());
		}
	}
	
	private void printPrompt(String msg) {
		System.out.println(msg);
		System.out.print(">");
	}
	
	public CommandLineListener() {
		printPrompt("New Game");
		Command c = parser.getCommand();
		while (c.getCommand() != CommandWord.QUIT) {
			execute(c);
			c = parser.getCommand();
		}
		System.out.println(c.getMsg());
	}
}
