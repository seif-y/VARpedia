package wikispeak.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import wikispeak.Bash;
import wikispeak.Creator;

public class CreationPreviewController extends Controller {

	@FXML
	private AnchorPane previewCreationsPage;
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
    	Creator.get().cleanup();
    	loadCreationsPage();
    }

    @FXML
    private void handleSave() {
    	Creator.get().cleanup();
    	loadCreationsPage();
    	
    }
    
    @Override
	protected void onSwitchScenes() {
    	player.stop();
    }
    

    private void loadCreationsPage() {
    	
    	Pane parent = (Pane) previewCreationsPage.getParent();
    	switchScenes(parent, "CreationsPage.fxml");
    }
}
