package wikispeak.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Creator;

public class FinishCreationController extends Controller {

	@FXML
	private AnchorPane pane;
	@FXML
    private TextField creationName;
    @FXML
	private ImageView loadingGif;
    @FXML
	private Text errorMsg;
    @FXML
	private ListView<String> audioList;
    @FXML
    private ListView<ImageView> imageList;
    private Map<ImageView, String> imageMap = new IdentityHashMap<>();
    
    
    @FXML
    private void initialize() {
    	errorMsg.setVisible(false);
    	loadingGif.setVisible(false);
    	
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
    	
    	String fileName = creationName.getText();
    	
    	if (fileName == null || fileName.equals("")) {
            errorMsg.setText("Please enter a name for your creation.");
            errorMsg.setVisible(true);
            
        } else if (Bash.hasInvalidChars(fileName, true)) {
        	errorMsg.setText("Creation name contains invalid characters.");
        	errorMsg.setVisible(true);
        		
        } else if (!(new File("./creations/" + fileName + ".mp4").exists())) {
        	errorMsg.setVisible(false);
        	loadingGif.setVisible(true);
        	Thread creatorThread = new Thread(new GenerateCreation());
        	creatorThread.start();
        } else {
        	Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Overwrite Warning");
            saveAlert.setHeaderText("The creation " + fileName + " already exists!");
            saveAlert.setContentText("If you press \"OK\" you will overwrite this file.");
            saveAlert.showAndWait().ifPresent(response -> {
            	if (response == ButtonType.OK) {
            		errorMsg.setVisible(false);
            		loadingGif.setVisible(true);
            		Thread creatorThread = new Thread(new GenerateCreation());
            		creatorThread.start();
            	}
            });
        }
    }
    
    
    private class GenerateCreation extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			//Add textField for user to select creation name
	    	String fileName = creationName.getText();
	    	String audioFileName = "." + fileName + "-audio.wav";
	    	String videoFileName = "." + fileName + "-video.mp4";
	    	
	    	//Create audio based on selected files by concatenating them
	    	List<String> selectedAudioFiles = new ArrayList<String>();
	    	for (String audioFile : audioList.getSelectionModel().getSelectedItems()) {
	    		selectedAudioFiles.add("." + audioFile + ".wav");
	    	}
	    	Creator.get().combineAudio(selectedAudioFiles, audioFileName);
	    	String time = Creator.get().getTimeOfAudio(audioFileName);
	    	time = time.substring(0, time.length() - 1);
	    	
	        //Create video based on selected images using the time of the audio file
	    	List<String> selectedImageFiles = new ArrayList<String>();
	    	for (ImageView imageView : imageList.getSelectionModel().getSelectedItems()) {
	    		selectedImageFiles.add(imageMap.get(imageView));
	    	}
	    	Creator.get().makeSlideshow(selectedImageFiles, videoFileName, time);
	    	
	    	//Combine audio and video files to one creation
	    	Creator.get().combine("./images/" + videoFileName, "./audiofiles/" + audioFileName, fileName + ".mp4");
	    	
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
		    	switchScenes(pane, "CreationPreview.fxml");
			});
		}
    	
    	
    }
}
