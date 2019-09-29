package wikispeak.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import wikispeak.Bash;
import wikispeak.Creator;

public class FinishCreationController {

	@FXML
	private AnchorPane previewCreationsPage;
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
    	String[] audioFiles = Bash.readOutput(Bash.execute("./creations/audiofiles", "ls .*.wav 2> /dev/null")).split("\n");
    	ObservableList<String> recordings = FXCollections.observableArrayList();
    	for (String fileName : audioFiles) {
    		recordings.add(fileName.substring(1, fileName.length() - 4));
    	}
    	audioList.setItems(recordings);
    	audioList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    
    private void setUpImageList() {
    	String[] imageFiles = Bash.readOutput(Bash.execute("./creations/images", "ls .*.jpg 2> /dev/null")).split("\n");
    	ObservableList<ImageView> images = FXCollections.observableArrayList();
    	for (String imageFile : imageFiles) {
    		File file = new File("./creations/images", imageFile);
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
    	Thread creatorThread = new Thread(new GenerateCreation());
    	creatorThread.start();
    }
    
    
    private class GenerateCreation extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			//Add textField for user to select creation name
	    	String creationName = "creation";
	    	String audioFileName = "." + creationName + "-audio.wav";
	    	String videoFileName = "." + creationName + "-video.mp4";
	    	
	    	//Create audio based on selected files by concatenating them
	    	List<String> selectedAudioFiles = new ArrayList<String>();
	    	for (String audioFile : audioList.getSelectionModel().getSelectedItems()) {
	    		selectedAudioFiles.add("." + audioFile + ".wav");
	    	}
	    	Creator.get().combineAudio(selectedAudioFiles, audioFileName);
	    	String time = Creator.get().getTimeOfAudio(audioFileName);
	    	System.out.println("Time: " + time);
	    	time = time.substring(0, time.length() - 1);
	    	
	        //Create video based on selected images using the time of the audio file
	    	List<String> selectedImageFiles = new ArrayList<String>();
	    	for (ImageView imageView : imageList.getSelectionModel().getSelectedItems()) {
	    		selectedImageFiles.add(imageMap.get(imageView));
	    	}
	    	System.out.println("Images: " + selectedImageFiles.toString());
	    	Creator.get().makeSlideshow(selectedImageFiles, videoFileName, time);
	    	
	    	//Combine audio and video files to one creation
	    	Creator.get().combine("./images/" + videoFileName, "./audiofiles/" + audioFileName, creationName);
	    	
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
				Pane parent = (Pane) previewCreationsPage.getParent();
		    	FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(this.getClass().getResource("/wikispeak/resources/CreationPreview.fxml"));
		        try {
		            AnchorPane viewCreationPage = loader.load();
		            parent.getChildren().clear();
		            parent.getChildren().add(viewCreationPage);

		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			});
		}
    	
    	
    }
}
