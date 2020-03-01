package minesweeper;

public class MineTile {
	private boolean mined = false;
	private boolean revealed = false;
	private boolean marked = false;
	private int mineNeighbours = 0;

	public boolean isMined() {
		return mined;
	}

	public void setMined(boolean mined) {
		this.mined = mined;
	}
	
	public boolean isRevealed() {
		return revealed;
	}
	
	public void reveal() {
		revealed = true;
	}
	
	public boolean isMarked() {
		return marked;
	}
	
	public void toggleMarked() {
		marked = !marked;
	}
	
	public void addMineNeighbour() {
		if (mineNeighbours == 8) {
			throw new ArithmeticException("Can't have more than 8 mine neighbours");
		} else {
			mineNeighbours++;
		}
	}
	
	public void removeMineNeighbour() {
		if (mineNeighbours == 0) {
			throw new ArithmeticException("Can't have less than 0 mine neighbours");
		} else {
			mineNeighbours--;
		}
	}
	
	public int getMineNeighbours() {
		return mineNeighbours;
	}
	
	public String toString(boolean forceReveal) {
		if (marked) {
			return "!";
		} else if (forceReveal || revealed) {
			if (mined) {
				return "*";
			} else if (mineNeighbours > 0) {
				return String.valueOf(mineNeighbours);
			} else {
				return " ";
			}
		} else {
			return "#";
		}
	}
	
	@Override
	public String toString() {
		return this.toString(false);
	}
}
