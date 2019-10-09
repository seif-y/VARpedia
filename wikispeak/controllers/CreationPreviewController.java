package wikispeak.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    	if (new File("./creations/creations.ser").exists()) {
    		try {
    			FileInputStream fileIn = new FileInputStream("./creations/creations.ser");
    			ObjectInputStream in = new ObjectInputStream(fileIn);
    			creations = (ArrayList<Creation>) in.readObject();
    			in.close();
    			fileIn.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		creations = new ArrayList<Creation>();
    	}
    	
    	creations.add(new Creation(Wikit.get().getTerm(), creationName, 50));
    	
    	try {
    		FileOutputStream fileOut = new FileOutputStream("./creations/creations.ser");
    		ObjectOutputStream out = new ObjectOutputStream(fileOut);
    		out.writeObject(creations);
    		out.close();
    		fileOut.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    @Override
	protected void onSwitchScenes() {
    	player.stop();
    }
    

    private void loadCreationsPage() {
    	switchScenes(pane, "CreationsPage.fxml");
    }
}
