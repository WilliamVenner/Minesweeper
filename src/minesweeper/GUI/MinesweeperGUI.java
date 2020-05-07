package minesweeper.GUI;

import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import minesweeper.GUI.GameSounds.SOUND;
import minesweeper.GUI.GridTile.TILE;

import minesweeper.Minesweeper;
import minesweeper.Minefield;

public class MinesweeperGUI extends Application {
    // https://bugs.openjdk.java.net/browse/JDK-8116047
    // https://bugs.openjdk.java.net/browse/JDK-8187899
    private final int JAVAFX_WINDOW_WIDTH_OFFSET = 16;
    private final int JAVAFX_WINDOW_HEIGHT_OFFSET = 38;
    
    private final int TILE_GAP_SIZE = 1;
    private final int TILE_MIN_SIZE = 24;
    
    private BorderPane gameWindow;
    private Stage gameStage;
    private Scene gameScene;
	private GridPane gameGrid;
    private MenuBar gameMenuBar;
    
    private final GameSounds gameSounds = new GameSounds();
    
	private Minefield minefield;
	private GridTile[][] tiles;
    
	private boolean playing = false;
	private void setPlaying(boolean playing) {
        this.playing = playing;
    }
    
	private void newGame(int rows, int columns, int maxMines) {
        gameSounds.stop();
        
		minefield = Minesweeper.newGame(rows, columns, maxMines);
        
		setupGrid(gameWindow, rows, columns);
	}
    
    private void quitGame() {
        gameStage.close();
    }
	
    private void newGameDialog() {
        Dialog<Integer[]> dialog = new Dialog<>();
        dialog.setTitle("New Game");
        dialog.setHeaderText("Please enter your desired parameters for the game.");

        ButtonType OK = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OK, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        NumericTextField rows = new NumericTextField();
        rows.setPromptText("10");
        
        NumericTextField columns = new NumericTextField();
        columns.setPromptText("15");
        
        NumericTextField maxMines = new NumericTextField();
        maxMines.setPromptText("20");
        
        GridPane.setHgrow(rows, Priority.ALWAYS);
        grid.add(new Label("Rows:"), 0, 0);
        grid.add(rows, 1, 0);
        
        GridPane.setHgrow(columns, Priority.ALWAYS);
        grid.add(new Label("Columns:"), 0, 1);
        grid.add(columns, 1, 1);
        
        GridPane.setHgrow(maxMines, Priority.ALWAYS);
        grid.add(new Label("Max mines:"), 0, 2);
        grid.add(maxMines, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(() -> rows.requestFocus());
        
        dialog.setResultConverter(btn -> {
            if (btn == OK) {
                return new Integer[] {
                    rows.getText().length() == 0 ? 10 : Integer.valueOf(rows.getText()),
                    columns.getText().length() == 0 ? 15 : Integer.valueOf(columns.getText()),
                    maxMines.getText().length() == 0 ? 20 : Integer.valueOf(maxMines.getText())
                };
            }
            return null;
        });
        
        Optional<Integer[]> result = dialog.showAndWait();
        if (result.isPresent()) {
            Integer[] parameters = result.get();
            newGame(parameters[0], parameters[1], parameters[2]);
        }
    }
    
    private void gameConclusionDialog(boolean won) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(won ? "Congratulations!" : "BOOM!");
        alert.setHeaderText(won ? "You win!" : "You lose!");
        alert.setContentText("Would you like to play again?");

        ButtonType playAgain = new ButtonType("Yes");
        ButtonType quit = new ButtonType("Quit");

        alert.getButtonTypes().setAll(playAgain, quit);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == playAgain){
            newGameDialog();
        } else if (result.get() == quit) {
            quitGame();
        }
    }
    
    private void gameWonDialog() {
        gameConclusionDialog(true);
    }
    
    private void gameLostDialog() {
        gameConclusionDialog(false);
    }
    
    private void checkWin() {
        if (minefield.areAllMinesRevealed()) {
            setPlaying(false);
            gameSounds.play(SOUND.WIN);
            gameWonDialog();
        }
    }
    
	private void gridClicked(GridTile tile, int row, int col) {
		if (minefield.step(row, col)) {
            gameSounds.play(SOUND.REVEAL);
			for (int tileRow = 0; tileRow < minefield.getRowCount(); tileRow++) {
				for (int tileColumn = 0; tileColumn < minefield.getColumnCount(); tileColumn++) {
					tiles[tileRow][tileColumn].update();
				}
			}
            checkWin();
		} else {
			tile.setTile(TILE.BOMB);
			setPlaying(false);
            gameSounds.play(SOUND.EXPLODE);
            gameLostDialog();
		}
	}
	
	private void gridRightClicked(GridTile tile, int row, int col) {
		minefield.markTile(row, col);
        gameSounds.play(tile.mineTile.isMarked() ? SOUND.MARK : SOUND.UNMARK);
		tile.update();
        checkWin();
	}
    
    private class gridClickEvent implements EventHandler<MouseEvent> {
        private final int grid_row;
        private final int grid_col;
        
        public gridClickEvent(int row, int col) {
            grid_row = row;
            grid_col = col;
        }
        
        @Override
        public void handle(MouseEvent e) {
            if (minefield != null && playing) {
                final GridTile tile = (GridTile) e.getSource();
                
                switch (e.getButton()) {
                    case PRIMARY:
                        gridClicked(tile, grid_row, grid_col);
                        break;

                    case SECONDARY:
                        gridRightClicked(tile, grid_row, grid_col);
                        break;
                }
            }
        }
    }
	
	private void setupGrid(BorderPane root, int rows, int columns) {
		if (gameGrid != null) {
			root.getChildren().remove(gameGrid);
		}
		
		setPlaying(true);
		
		gameGrid = new GridPane();
        gameGrid.setHgap(TILE_GAP_SIZE);
        gameGrid.setVgap(TILE_GAP_SIZE);
        gameGrid.setGridLinesVisible(true);
        
        final int minTileWidth = (TILE_MIN_SIZE * (columns + 2)) + (TILE_GAP_SIZE * ((columns + 2) + 1));
        final int minTileHeight = (TILE_MIN_SIZE * (rows + 2)) + (TILE_GAP_SIZE * ((rows + 2) + 1));
        gameGrid.setMinWidth(minTileWidth);
        gameGrid.setMinHeight(minTileHeight);
        
        gameStage.setMinWidth(minTileWidth + JAVAFX_WINDOW_WIDTH_OFFSET);
        gameStage.setMinHeight(minTileHeight + gameMenuBar.getHeight() + JAVAFX_WINDOW_HEIGHT_OFFSET);
        gameStage.setWidth(minTileWidth + JAVAFX_WINDOW_WIDTH_OFFSET);
        gameStage.setHeight(minTileHeight + gameMenuBar.getHeight() + JAVAFX_WINDOW_HEIGHT_OFFSET);
		
		root.setCenter(gameGrid);
		
		// Create boundaries
		for (int row = 0; row <= minefield.getRowCount() + 1; row++) {
			if (row == 0 || row == minefield.getRowCount() + 1) {
				for (int col = 0; col <= minefield.getColumnCount() + 1; col++) {
					new GridBoundary(gameGrid, row, col);
				}
			} else {
				new GridBoundary(gameGrid, row, 0);
				new GridBoundary(gameGrid, row, minefield.getColumnCount() + 1);
			}
		}
		
		// Create tiles
		tiles = new GridTile[minefield.getRowCount()][minefield.getColumnCount()];
		
		for (int row = 0; row < minefield.getRowCount(); row++) {
			for (int col = 0; col < minefield.getColumnCount(); col++) {
				final GridTile tile = new GridTile(minefield.getTile(row, col));
                tile.setOnMouseClicked(new gridClickEvent(row, col));
                
				tiles[row][col] = tile;
				gameGrid.add(tile, col + 1, row + 1);
			}
		}
	}
	
    private void createMenuBar() {
        gameMenuBar = new MenuBar();
		gameMenuBar.setUseSystemMenuBar(true);
		
			Menu gameMenu = new Menu("Game");
				MenuItem newGame = new MenuItem("New game...");
				newGame.setOnAction(e -> {
                    newGameDialog();
                });
				
				MenuItem quit = new MenuItem("Quit");
				quit.setOnAction(e -> {
                    quitGame();
                });

			gameMenu.getItems().add(newGame);
			gameMenu.getItems().add(quit);
			
		gameMenuBar.getMenus().add(gameMenu);
        
		gameWindow.setTop(gameMenuBar);
    }
    
    private void createScene() {
		gameScene = new Scene(gameWindow);
		gameStage.setScene(gameScene);
    }
    
	@Override
	public void start(Stage stage) {
        this.gameStage = stage;
        
		gameStage.setTitle("Minesweeper");
		gameStage.setResizable(true);
        
		gameWindow = new BorderPane();
		
        createScene();
		createMenuBar();
		
		gameStage.show();
        
		newGame(10, 15, 20);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
