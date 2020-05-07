package minesweeper.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import minesweeper.MineTile;

public class GridTile extends AnchorPane {
    public static enum TILE {
        TILE,
        REVEALED,
        MARKED,
        BOMB,
        BOUNDARY
    }
    
	public MineTile mineTile;
	
	private ImageView imageView;
	private Label neighbouringMinesLabel;
    
	public GridTile(MineTile mineTile) {
		this.mineTile = mineTile;
		setup(TILE.TILE);
	}
	
	public GridTile(TILE tile) {
		setup(tile);
	}
	
	private void setup(TILE tile) {        
        this.setMinSize(0, 0);
        this.setPadding(new Insets(2));
        
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        
		createTileImage();
        if (tile != TILE.BOUNDARY) {
            createNeighbouringMinesLabel();
        }
		setTile(tile);
	}
	
	private void createTileImage() {
		imageView = new ImageView();
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        
		this.getChildren().add(imageView);
	}
	
	private void createNeighbouringMinesLabel() {
		neighbouringMinesLabel = new Label();
		neighbouringMinesLabel.setVisible(false);
        
        neighbouringMinesLabel.setAlignment(Pos.CENTER);
        neighbouringMinesLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, .5), CornerRadii.EMPTY, Insets.EMPTY)));
		neighbouringMinesLabel.setStyle("-fx-font-family: 'monospaced'; -fx-font-weight: bold; -fx-stroke: black; -fx-stroke-width: 2px");
        
        AnchorPane.setTopAnchor(neighbouringMinesLabel, 0.0);
        AnchorPane.setLeftAnchor(neighbouringMinesLabel, 0.0);
        AnchorPane.setRightAnchor(neighbouringMinesLabel, 0.0);
        AnchorPane.setBottomAnchor(neighbouringMinesLabel, 0.0);
        
		this.getChildren().add(neighbouringMinesLabel);
    }
    
	public void setTile(TILE tile) {
		this.imageView.setImage(Spritesheet.getTile(tile));
	}
	
	public void update() {
		neighbouringMinesLabel.setVisible(false);
		final int mineNeighbours = mineTile.getMineNeighbours();
		if (mineTile.isRevealed() && mineNeighbours > 0) {
			final int r = (int)(((float)mineNeighbours / 4.0) * 255.0);
			final int g = (int)(((1.0 - ((float)mineNeighbours / 4.0)) * 255.0)); // bug here?
			neighbouringMinesLabel.setTextFill(Color.rgb(r, g, 0));
			neighbouringMinesLabel.setText(String.valueOf(mineNeighbours));
			neighbouringMinesLabel.setVisible(true);
		}
		
		if (mineTile.isMarked()) {
			setTile(TILE.MARKED);
		} else if (mineTile.isRevealed()) {
			setTile(TILE.REVEALED);
		} else {
			setTile(TILE.TILE);
		}
	}
}
