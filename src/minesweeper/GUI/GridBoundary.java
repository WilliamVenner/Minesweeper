package minesweeper.GUI;

import javafx.scene.layout.GridPane;

public class GridBoundary extends GridTile {
    public GridBoundary(GridPane grid, int row, int col) {
        super(TILE.BOUNDARY);
		grid.add(this, col, row);
    }
}
