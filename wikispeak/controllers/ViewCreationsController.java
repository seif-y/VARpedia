package wikispeak.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import wikispeak.Bash;
import wikispeak.Creation;

public class ViewCreationsController extends Controller {
	
    private String _currentCreation;
    private boolean _creationSelected;


    @FXML
    private AnchorPane pane;
    @FXML
    private ListView<String> creationList;
    @FXML
    private ArrayList<Creation> creations;
    @FXML
    private Text creationDisplay;
    @FXML
    private MediaView viewer;
    
    private MediaPlayer player;

    
    /**
     * Initialize method: Makes the creations directory if it doesn't exist, then adds every creation in the directory to the list view.
     * If the creations directory is empty, nothing is added to the list view, and a message is displayed to communicate this.
     */
    @FXML
    private void initialize() {

    	if (!(new File("./creations")).exists()) {
    		try {
				Bash.execute(".","mkdir creations").waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	};
    	
        _creationSelected = false;
        _currentCreation = null;
        
       creations = readCreationList();
        
        
        for (Creation creation : creations) {
        	creationList.getItems().add(creation.getName());
        }
        if (creationList.getItems().isEmpty())
        	creationDisplay.setText("No creations to display");
        creationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    
    private ArrayList readCreationList() {
    	ArrayList<Creation> list = null;
    	if (new File("./creations/creations.ser").exists()) {
    		try {
    			FileInputStream fileIn = new FileInputStream("./creations/creations.ser");
    			ObjectInputStream in = new ObjectInputStream(fileIn);
    			list = (ArrayList<Creation>) in.readObject();
    			in.close();
    			fileIn.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		list = new ArrayList<Creation>();
    	}
    	
    	return list;
    }
    
    
    @FXML
    private void handleHome() {
    	switchScenes(pane, "HomePage.fxml");
    }

    
    /**
     * Executes when the list view is clicked on.
     * Updates state to indicate that a creation is selected, and show which creation is selected
     */
    @FXML
    private void handleSelectCreation() {
    	if (player != null)
    		player.stop();
    	
    	try {
        	_currentCreation = creationList.getSelectionModel().getSelectedItem().toString();
        	if (!_creationSelected) { _creationSelected = true; }
        	creationDisplay.setText(_currentCreation);

            File vidFile = new File("./creations/" + _currentCreation + ".mp4");
            Media video = new Media(vidFile.toURI().toString());
            player = new MediaPlayer(video);
            viewer.setMediaPlayer(player);
        	
        } catch (NullPointerException e) {}        
    }
    

    /**
     * Executes when the delete button is pressed.
     * If a valid creation is selected, it runs the delete task for that creation in another thread.
     */
    @FXML
    private void handleDelete() {
        if (_creationSelected) {
        	player.stop();
            String creationName = creationDisplay.getText();
            Alert delAlert = new Alert(Alert.AlertType.CONFIRMATION);
            delAlert.setTitle("Delete Creation");
            delAlert.setHeaderText("Are you sure?");
            delAlert.setContentText("You are about to delete " + creationName);
            delAlert.showAndWait().ifPresent(response -> {
            	if (response == ButtonType.OK) {
                	Thread deleteThread = new Thread(new DeleteCreation<Void>(_currentCreation));
                	deleteThread.start();
            	}
            });
        }
    }


    /**
     * Executes when the back button is pressed.
     * Skips back 3 seconds.
     */
    @FXML
    private void handleBack() {
    	player.seek( player.getCurrentTime().add( Duration.seconds(-3)) );
    }
    
    /**
     * Executes when the forward button is pressed.
     * Skips forward 3 seconds.
     */
    @FXML
    private void handleForward() {
    	player.seek( player.getCurrentTime().add( Duration.seconds(3)) );
    }
    
    
    
    /**
     * Executes when the play button is pressed.
     * If a valid creation is selected, it runs the play task for that creation in another thread.
     */
    @FXML
    private void handlePlay() {
        if (_creationSelected) {
            if (player.getStatus() == Status.PLAYING) {
            	player.pause();
            } else {
            	player.play();
            }
        }
    }
    
    
    @Override
	protected void onSwitchScenes() {
    	player.stop();
    	
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
    
    
    
    /**
     * Subclass of Task to handle deleting creations
     */
    @SuppressWarnings("hiding")
	private class DeleteCreation<Void> extends Task<Void> {
    	
    	String _creation;
    	
    	private DeleteCreation(String creation) {
    		_creation = creation;
    	}

    	
    	/**
    	 * Call method: Deletes the creation using rm command
    	 */
		@Override
		protected Void call() throws Exception {
			Bash.execute("./creations", "rm " + _creation + ".mp4");
			return null;
		}
		
		
		/**
		 * Done method: Updates creation list view and resets display message
		 */
		@Override
		protected void done() {
			Platform.runLater(() -> {
				creationList.getItems().remove(_creation);
				creationDisplay.setText("Select a creation");
				_creationSelected = false;
			});
		}
    	
    }
}
