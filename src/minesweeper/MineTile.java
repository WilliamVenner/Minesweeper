package minesweeper;

public class MineTile {
	private boolean mined = false;
	private boolean revealed = false;
	private boolean marked = false;
	private int mineNeighbours = 0;

	public boolean isMined() {
		return mined;
	}

	public void mine() {
		mined = true;
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
		this.mineNeighbours++;
	}
	
	public int getMineNeighbours() {
		return mineNeighbours;
	}
	
	@Override
	public String toString() {
		return this.toString(false);
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
}
