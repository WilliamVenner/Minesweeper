package minesweeper.GUI;

import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Pair;

public class GameSounds {
    Random soundRandomizer = new Random();
    
    public GameSounds() {
        loadWinSounds();
    }
    
	public static enum SOUND {
		EXPLODE,
		REVEAL,
		MARK,
		UNMARK,
        WIN
	}
    
	private static final EnumMap<SOUND, Pair<String, Integer>> SOUND_FILE_LOCATORS = new EnumMap<SOUND, Pair<String, Integer>>(SOUND.class);
	static {
		SOUND_FILE_LOCATORS.put(SOUND.EXPLODE, new Pair<>("explode", 4));
		SOUND_FILE_LOCATORS.put(SOUND.REVEAL, new Pair<>("reveal", 4));
		SOUND_FILE_LOCATORS.put(SOUND.MARK, new Pair<>("mark", 3));
		SOUND_FILE_LOCATORS.put(SOUND.UNMARK, new Pair<>("unmark", 4));
	};
    
	private static final EnumMap<SOUND, MediaPlayer[]> MEDIA_PLAYERS = new EnumMap<SOUND, MediaPlayer[]>(SOUND.class);
	static {
        SOUND_FILE_LOCATORS.entrySet().forEach((entry) -> {
            SOUND sound = entry.getKey();
            String fileName = entry.getValue().getKey();
            Integer fileCount = entry.getValue().getValue();
            
            MediaPlayer[] mediaPlayers = new MediaPlayer[fileCount];
            for (int i = 0; i < fileCount; i++) {
                try {
                    mediaPlayers[i] = new MediaPlayer(new Media(GameSounds.class.getResource("/minesweeper/GUI/Sounds/" + fileName + (i + 1) + ".mp3").toURI().toString()));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(GameSounds.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            MEDIA_PLAYERS.put(sound, mediaPlayers);
        });
	};
    
    private MediaPlayer win1;
    private MediaPlayer win2;
    private void loadWinSounds() {
        try {
            win1 = new MediaPlayer(new Media(this.getClass().getResource("/minesweeper/GUI/Sounds/win1.mp3").toURI().toString()));
            win2 = new MediaPlayer(new Media(this.getClass().getResource("/minesweeper/GUI/Sounds/win2.mp3").toURI().toString()));
            win1.setOnEndOfMedia(win2::play);
        } catch (URISyntaxException ex) {
            Logger.getLogger(GameSounds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void play(SOUND sound) {
        if (sound == SOUND.WIN) {
            win1.stop();
            win2.stop();
            win1.play();
        } else {
            MediaPlayer[] randomSounds = MEDIA_PLAYERS.get(sound);
            MediaPlayer media = randomSounds[soundRandomizer.nextInt(randomSounds.length)];
            media.stop();
            media.play();
        }
    }
    
    public void stop() {
        win1.stop();
        win2.stop();        
        MEDIA_PLAYERS.values().forEach((medias) -> {
            for (MediaPlayer media : medias) {
                media.stop();
            }
        });
    }
}
