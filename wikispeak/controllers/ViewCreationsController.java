package wikispeak.controllers;

import java.io.File;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import wikispeak.Bash;

public class ViewCreationsController {
	
    private String _currentCreation;
    private boolean _creationSelected;


    @FXML
    private ListView<String> creationList;
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
    		Bash.execute(".","mkdir creations");
    	};
    	
        _creationSelected = false;
        _currentCreation = null;
        
        String[] creations = Bash.readOutput(Bash.execute("./creations", "ls")).split("\n");
        
        try {
        	for (String creation : creations) {
        		creationList.getItems().add(creation.substring(0, creation.length() - 4));
        	}
        } catch (StringIndexOutOfBoundsException e) { 
        	creationDisplay.setText("There are no creations to display.");
        }
        
        creationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    
    /**
     * Executes when the list view is clicked on.
     * Updates state to indicate that a creation is selected, and show which creation is selected
     */
    @FXML
    private void handleSelectCreation() {
    	try {
        	_currentCreation = creationList.getSelectionModel().getSelectedItem().toString();
        	if (!_creationSelected) { _creationSelected = true; }
        	creationDisplay.setText(_currentCreation);

            File vidFile = new File("./creations/" + _currentCreation + ".mp4");
            Media video = new Media(vidFile.toURI().toString());
            player = new MediaPlayer(video);
            viewer.setMediaPlayer(player);
            viewer.setVisible(true);
        	
        } catch (NullPointerException e) {}        
    }
    

    /**
     * Executes when the delete button is pressed.
     * If a valid creation is selected, it runs the delete task for that creation in another thread.
     */
    @FXML
    private void handleDelete() {
        if (_creationSelected) {
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
     * Executes when the play button is pressed.
     * If a valid creation is selected, it runs the play task for that creation in another thread.
     */
    @FXML
    private void handlePlay() {
    	System.out.println("Checking");
        if (_creationSelected) {
        	if (player.getStatus() == Status.PLAYING) {
        		player.pause();
        	} else {
        		System.out.println("Playing");
        		player.play();
        	}
        }
    }


    /**
     * Subclass of Task to handle playing creations
     */
    @SuppressWarnings("hiding")
	private class PlayCreation<Void> extends Task<Void> {

    	String _creation;

    	private PlayCreation(String creation) {
    		_creation = creation;
    	}

    	/**
    	 * Call method: Uses ffplay to play the creation in a new window
    	 */
    	@Override
        protected Void call() throws Exception {
            //Bash.execute("./creations", "ffplay -loglevel panic -autoexit " + _creation + ".mp4");
    		
    		

            return null;
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
