package wikispeak.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import wikispeak.Bash;
import wikispeak.Creator;

public class FinishCreationController {

    @FXML
    private ListView<String> audioList;

    @FXML
    private ListView<ImageView> imageList;
    private Map<ImageView, String> imageMap = new IdentityHashMap<>();
    
    
    @FXML
    private void initialize() {
    	setUpAudioList();
    	setUpImageList();
    }
    
    
    private void setUpAudioList() {
    	String[] audioFiles = Bash.readOutput(Bash.execute("./creations", "ls .*.wav 2> /dev/null")).split("\n");
    	ObservableList<String> recordings = FXCollections.observableArrayList();
    	for (String fileName : audioFiles) {
    		recordings.add(fileName.substring(1, fileName.length() - 4));
    	}
    	audioList.setItems(recordings);
    	audioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    
    private void setUpImageList() {
    	String[] imageFiles = Bash.readOutput(Bash.execute("./creations", "ls .*.jpg 2> /dev/null")).split("\n");
    	ObservableList<ImageView> images = FXCollections.observableArrayList();
    	for (String imageFile : imageFiles) {
    		File file = new File("./creations", imageFile);
    		if (file.exists()) {
    			Image image = new Image(file.toURI().toString());
    			ImageView imageView = new ImageView(image);
    			imageView.setPreserveRatio(true);
    			imageView.setFitWidth(180);
    			images.add(imageView);
    			imageMap.put(imageView, imageFile);
    		}
    	}
    	imageList.setItems(images);
    	imageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void handleCreation() {
    	//TODO: Add textField for user to select creation name
    	String creationName = "creation";
    	String audioFileName = "." + creationName + "-audio.wav";
    	String videoFileName = "." + creationName + "-video.mp4";
    	
    	List<String> selectedAudioFiles = new ArrayList<String>();
    	for (String audioFile : audioList.getSelectionModel().getSelectedItems()) {
    		selectedAudioFiles.add("." + audioFile + ".wav");
    	}
    	System.out.println("Audio: " + selectedAudioFiles);
    	Creator.get().combineAudio(selectedAudioFiles, audioFileName);
    	String time = Creator.get().getTimeOfAudio(audioFileName);
    	System.out.println("Time: " + time);
    	time = time.substring(0, time.length() - 1);
    	
        //TODO: Create video based on selected images using the time of the audio file
    	List<String> selectedImageFiles = new ArrayList<String>();
    	for (ImageView imageView : imageList.getSelectionModel().getSelectedItems()) {
    		selectedImageFiles.add(imageMap.get(imageView));
    	}
    	System.out.println("Images: " + selectedImageFiles.toString());
    	Creator.get().makeSlideshow(selectedImageFiles, videoFileName, time);
    	
    	

        //TODO: Load the CreationPreview page
    }
    
    
    private class GenerateCreation extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
    	
    	
    }
}
