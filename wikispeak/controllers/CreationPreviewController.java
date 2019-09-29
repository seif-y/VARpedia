package wikispeak.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import wikispeak.Bash;
import wikispeak.Creator;

public class CreationPreviewController {

    @FXML
    private MediaView viewer;
    private MediaPlayer player;
    
    private String creationName;

    @FXML
    private void initialize() {
    	creationName = Creator.get().getCurrentCreationName();
    	File vidFile = new File("./creations/" + creationName);
        Media video = new Media(vidFile.toURI().toString());
        player = new MediaPlayer(video);
        viewer.setMediaPlayer(player);
    }

    @FXML
    private void handlePlay() {
    	if (player.getStatus() == Status.PLAYING) {
        	player.pause();
        } else {
        	player.play();
        }
    }

    @FXML
    private void handleBack() {
    	player.seek( player.getCurrentTime().add( Duration.seconds(-3)) );
    }

    @FXML
    private void handleFwd() {
    	player.seek( player.getCurrentTime().add( Duration.seconds(3)) );
    }

    @FXML
    private void handleScrap() {
    	Bash.execute("./creations", "rm " + creationName);
    	loadCreationsPage();
    }

    @FXML
    private void handleSave() {
    	loadCreationsPage();
    }
    
    private void loadCreationsPage() {
    	
    }
}
