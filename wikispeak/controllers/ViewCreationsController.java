package wikispeak.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import wikispeak.Bash;
import wikispeak.Creation;
import wikispeak.Creator;

public class ViewCreationsController extends Controller {
	
    private Creation _currentCreation;
    private boolean _creationSelected;


    @FXML
    private AnchorPane pane;
	@FXML
	private Text creationDisplay;

	private ArrayList<Creation> creations;
	private ObservableList<Creation> tableItems;
    @FXML
    private TableView<Creation> table;
    @FXML
	private TableColumn<String, Creation> names;
    @FXML
	private TableColumn<Integer, Creation> ratings;
    @FXML
	private TableColumn<Integer, Creation> views;

    @FXML
    private MediaView viewer;
    private MediaPlayer player;
    @FXML
    private Slider ratingSlider;

    
    /**
     * Initialize method: Makes the creations directory if it doesn't exist, then adds every creation in the directory to the list view.
     * If the creations directory is empty, nothing is added to the list view, and a message is displayed to communicate this.
     */
    @FXML
    private void initialize() {

    	Creator.get();
    	
        _creationSelected = false;
        _currentCreation = null;
        
       creations = readCreationList();
       tableItems = FXCollections.observableArrayList(creations);
       populateTable();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
        	if (_currentCreation != null) {
        		_currentCreation.setRating(newValue.intValue());
        		table.refresh();
        	}
        });
    }
    
    
    private ArrayList<Creation> readCreationList() {
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
    
    
    private void populateTable() {
    	if (creations.isEmpty()) {
			creationDisplay.setText("No creations to display");
		} else {
        	names.setCellValueFactory(new PropertyValueFactory<>("Name"));
        	ratings.setCellValueFactory(new PropertyValueFactory<>("Rating"));
        	views.setCellValueFactory(new PropertyValueFactory<>("Views"));
        	table.setItems(tableItems);
		}
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
        	_currentCreation = table.getSelectionModel().getSelectedItem();
        	if (!_creationSelected) { _creationSelected = true; }
        	creationDisplay.setText(_currentCreation.getName());
        	
        	ratingSlider.setValue(_currentCreation.getRating());

            File vidFile = new File("./creations/" + _currentCreation.getFile());
            Media video = new Media(vidFile.toURI().toString());
            player = new MediaPlayer(video);
            player.setOnEndOfMedia(() -> {
            	player.pause();
            	_currentCreation.incrementViews();
            	table.refresh();
            });
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
    
    
    @FXML
    private void handleRatingChange() {
    	System.out.println("whats poppin cuzzo lmao");
    	if (_currentCreation != null) {
    		_currentCreation.setRating((int) ratingSlider.getValue());
    	}
    }
    
    @Override
	protected void onSwitchScenes() {
    	if (player != null)
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
    	
    	Creation _creation;
    	
    	private DeleteCreation(Creation creation) {
    		_creation = creation;
    	}

    	
    	/**
    	 * Call method: Deletes the creation using rm command
    	 */
		@Override
		protected Void call() throws Exception {
			Bash.execute("./creations", "rm " + _creation.getFile());
			creations.remove(_creation);
			tableItems.remove(_creation);
			return null;
		}
		
		
		/**
		 * Done method: Updates creation list view and resets display message
		 */
		@Override
		protected void done() {
			Platform.runLater(() -> {
				creationDisplay.setText("Select a creation");
				_creationSelected = false;
			});
		}
    	
    }
}
