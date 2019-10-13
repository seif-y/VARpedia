package wikispeak.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import wikispeak.Bash;
import wikispeak.Creation;
import wikispeak.Creator;
import wikispeak.Wikit;

public class CreationPreviewController extends Controller {

	@FXML
	private AnchorPane pane;
    @FXML
    private MediaView viewer;
    private MediaPlayer player;
    @FXML
	private Slider ratingSlider;
    
    private String creationName;
    
    private ArrayList<Creation> creations;

    @FXML
    private void initialize() {
    	creationName = Creator.get().getCurrentCreationName();
    	File vidFile = new File("./creations/" + creationName);
        Media video = new Media(vidFile.toURI().toString());
        player = new MediaPlayer(video);
        viewer.setMediaPlayer(player);
    }
    
    @FXML
    private void handleGoBack() {
    	try {
			Bash.execute("./creations/images", "rm .*-video.mp4").waitFor();
			Bash.execute("./creations/audiofiles", "rm .*-audio.wav").waitFor();
	    	Bash.execute("./creations", "rm " + creationName).waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	switchScenes(pane, "FinishCreation.fxml");
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
    	saveCreation();
    	loadCreationsPage();
    	
    }
    
    
    private void saveCreation() {
    	creations = Creator.get().readCreationList();
    	
    	creations.add(new Creation(Wikit.get().getTerm(), creationName, (int) ratingSlider.getValue()));
    	
    	Creator.get().writeCreationList(creations);
    }
    
    
    @Override
	protected void onSwitchScenes() {
    	player.stop();
    }
    

    private void loadCreationsPage() {
    	switchScenes(pane, "CreationsPage.fxml");
    }
}
