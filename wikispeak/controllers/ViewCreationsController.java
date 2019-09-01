package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.text.Text;
import wikispeak.Bash;

import java.util.ArrayList;
import java.util.List;

public class ViewCreationsController {
	
    private String _currentCreation;
    private boolean _creationSelected;


    @FXML
    private ListView<String> creationList;
    @FXML
    private Text creationDisplay;

    
    @FXML
    private void initialize() {

        _creationSelected = false;
        _currentCreation = null;
        
        String[] creations = Bash.readOutput(Bash.execute("./creations", "ls")).split("\n");
        
        for (String creation : creations) {
        	creationList.getItems().add(creation.substring(0, creation.length() - 4));
        }
        creationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    

    @FXML
    private void handleSelectCreation() {
    	try {
        	_currentCreation = creationList.getSelectionModel().getSelectedItem().toString();
        	if (!_creationSelected) { _creationSelected = true; }
        	creationDisplay.setText(_currentCreation);
        	
        } catch (NullPointerException e) {}        
    }
    

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

    
    @FXML
    private void handlePlay() {
        if (_creationSelected) {
            Thread playerThread = new Thread(new PlayCreation<Void>(_currentCreation));
            playerThread.start();
        }
    }
    

    private class PlayCreation<Void> extends Task<Void> {

    	String _creation;
    	
    	private PlayCreation(String creation) {
    		_creation = creation;
    	}
    	
    	@Override
        protected Void call() throws Exception {
            Bash.execute("./creations", "ffplay -loglevel panic -autoexit " + _creation + ".mp4");
            return null;
        }
    }
    
    private class DeleteCreation<Void> extends Task<Void> {
    	
    	String _creation;
    	
    	private DeleteCreation(String creation) {
    		_creation = creation;
    	}

		@Override
		protected Void call() throws Exception {
			Bash.execute("./creations", "rm " + _creation + ".mp4");
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
				creationList.getItems().remove(_creation);
			});
		}
    	
    }
}
