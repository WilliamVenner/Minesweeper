package minesweeper.GUI;

import java.util.EnumMap;
import javafx.scene.image.Image;
import minesweeper.GUI.GridTile.TILE;

public class Spritesheet {
	private static final String PATH_PREFIX = "/minesweeper/GUI/Tiles/";
	
	private static final EnumMap<TILE, Image> TILE_IMAGES = new EnumMap<TILE, Image>(TILE.class);
	static {
		TILE_IMAGES.put(TILE.TILE, new Image(PATH_PREFIX + "TILE.png"));
		TILE_IMAGES.put(TILE.REVEALED, new Image(PATH_PREFIX + "TILE_REVEALED.png"));
		TILE_IMAGES.put(TILE.MARKED, new Image(PATH_PREFIX + "TILE_MARKED.png"));
		TILE_IMAGES.put(TILE.BOMB, new Image(PATH_PREFIX + "TILE_BOMB.png"));
		TILE_IMAGES.put(TILE.BOUNDARY, new Image(PATH_PREFIX + "TILE_BOUNDARY.png"));
	};
	
	public static Image getTile(TILE tile) {
		return TILE_IMAGES.get(tile);
	}
}
