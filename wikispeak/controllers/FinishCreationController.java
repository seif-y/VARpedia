package wikispeak.controllers;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import wikispeak.Bash;

public class FinishCreationController {

    @FXML
    private ListView<String> audioList;

    @FXML
    private ListView<ImageView> imageList;
    
    @FXML
    private void initialize() {
    	String[] audioFiles = Bash.readOutput(Bash.execute("./creations", "ls .*.wav 2> /dev/null")).split("\n");
    	for (String fileName : audioFiles) {
    		audioList.getItems().add(fileName.substring(1, fileName.length() - 4));
    	}
    	
    	String[] imageFiles = Bash.readOutput(Bash.execute("./creations", "ls *.jpg 2> /dev/null")).split("\n");
    	ObservableList<ImageView> images = FXCollections.observableArrayList();
    	for (String imageFile : imageFiles) {
    		File file = new File("./creations", imageFile);
    		if (file.exists()) {
    			Image image = new Image(file.toURI().toString());
    			ImageView imageview = new ImageView(image);
    			imageview.setPreserveRatio(true);
    			imageview.setFitWidth(180);
    			images.add(imageview);
    		}
    	}
    	imageList.setItems(images);    	
    }

    @FXML
    private void handleCreation() {
        //TODO: Create video based on selected images/audio files

        //TODO: Load the CreationPreview page
    }
}
