package wikispeak.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import wikispeak.Bash;

public class FinishCreationController {

    @FXML
    private ListView<String> audioList;

    @FXML
    private ListView<ImageView> imageList;
    
    @FXML
    private void initialize() {
    	String[] audioFiles = Bash.readOutput(Bash.execute("./creations", "ls .*.wav")).split("\n");
    	for (String fileName : audioFiles) {
    		audioList.getItems().add(fileName.substring(0, fileName.length() - 4));
    	}
    	
    	String[] imageFiles = Bash.readOutput(Bash.execute("./creations", "ls .*.jpg")).split("\n");
    	for (String fileName : imageFiles) {
    		imageList.getItems().add(new ImageView("./creations/" + fileName));
    	}
    	
    }

    @FXML
    private void handleCreation() {
        //TODO: Create video based on selected images/audio files

        //TODO: Load the CreationPreview page
    }
}
