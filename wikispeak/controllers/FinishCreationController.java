package wikispeak.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.control.ComboBox;
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
    private ObservableList<String> unselectedAudio;
    @FXML
    private ListView<String> selectedAudioList;
    private ObservableList<String> selectedAudio;
    
    @FXML
    private ComboBox<String> musicOptions;
    private Map<String, String> musicMap = new IdentityHashMap<>();
    
    @FXML
    private ListView<ImageView> imageList;
    private ObservableList<ImageView> unselectedImages;
    @FXML
    private ListView<ImageView> selectedImageList;
    private ObservableList<ImageView> selectedImages;
    
    private Map<ImageView, String> imageMap = new IdentityHashMap<>();
    
    
    
    @FXML
    private void initialize() {
    	errorMsg.setVisible(false);
    	loadingGif.setVisible(false);
    	
    	setUpAudioList();
    	setUpImageList();
    	
    	selectedAudio = FXCollections.observableArrayList();
    	selectedAudioList.setItems(selectedAudio);
    	selectedAudioList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	
        ObservableList<String> music = FXCollections.observableArrayList();
        music.addAll("One", "Two");
        //musicOptions.setStyle("-fx-font: 20px \"System\";");
        musicOptions.setItems(music);
        
        musicMap.put("One", "perspective.mp3");
        musicMap.put("Two", "whatyousay.mp3");
    	
    	selectedImages = FXCollections.observableArrayList();
    	selectedImageList.setItems(selectedImages);
    	selectedImageList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    
    
    private void setUpAudioList() {
    	String[] audioFiles = Bash.readOutput(Bash.execute("./creations/audiofiles", "ls .*.wav 2> /dev/null")).split("\n");
    	unselectedAudio = FXCollections.observableArrayList();
    	for (String fileName : audioFiles) {
    		unselectedAudio.add(fileName.substring(1, fileName.length() - 4));
    	}
    	audioList.setItems(unselectedAudio);
    	audioList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    
    
    private void setUpImageList() {
    	String[] imageFiles = Bash.readOutput(Bash.execute("./creations/images", "ls .*.jpg 2> /dev/null")).split("\n");
    	unselectedImages = FXCollections.observableArrayList();
    	for (String imageFile : imageFiles) {
    		File file = new File("./creations/images", imageFile);
    		if (file.exists()) {
    			Image image = new Image(file.toURI().toString());
    			ImageView imageView = new ImageView(image);
    			imageView.setPreserveRatio(true);
    			imageView.setFitWidth(180);
    			unselectedImages.add(imageView);
    			imageMap.put(imageView, imageFile);
    		}
    	}
    	imageList.setItems(unselectedImages);
    	imageList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    

    @FXML
    private void handleBack() {
    	switchScenes(pane, "AudioEditor.fxml");
    }
    
    
    @FXML
    private void handleScrapCreation() {
    	Creator.get().cleanup();
    	switchScenes(pane, "HomePage.fxml");
    }
    
    
    private void swapListItems(ListView listView, ObservableList list, int mod) {
    	int index = listView.getSelectionModel().getSelectedIndex();
    	try {
    		Collections.swap(list, index, index + mod);
    	} catch (IndexOutOfBoundsException e) {}
    }
    
    
    
    @FXML
    private void handleAddAudio() {
    	String chosenAudioFile = audioList.getSelectionModel().getSelectedItem();
    	if (chosenAudioFile != null) {
    		unselectedAudio.remove(chosenAudioFile);
    		selectedAudio.add(chosenAudioFile);
    	}
    }
    
    
    @FXML
    private void handleAudioUp() {
    	swapListItems(selectedAudioList, selectedAudio, -1);
    }
    
    @FXML
    private void handleAudioDown() {
    	swapListItems(selectedAudioList, selectedAudio, 1);
    }
    
    @FXML
    private void handleAudioDel() {
    	String chosenAudioFile = selectedAudioList.getSelectionModel().getSelectedItem();
    	if (chosenAudioFile != null) {
    		selectedAudio.remove(chosenAudioFile);
    		unselectedAudio.add(chosenAudioFile);
    	}
    }
    
    
    @FXML
    private void handleAddImages() {
    	ImageView chosenImage = imageList.getSelectionModel().getSelectedItem();
    	if (chosenImage != null) {
    		unselectedImages.remove(chosenImage);
    		selectedImages.add(chosenImage);
    	}
    }
    
    
    @FXML
    private void handleImageUp() {
    	swapListItems(selectedImageList, selectedImages, -1);
    }
    
    
    @FXML
    private void handleImageDown() {
    	swapListItems(selectedImageList, selectedImages, 1);
    }
    
    
    @FXML
    private void handleImageDel() {
    	ImageView chosenImage = selectedImageList.getSelectionModel().getSelectedItem();
    	selectedImages.remove(chosenImage);
    	unselectedImages.add(chosenImage);
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
        }else if (selectedImages.isEmpty() || selectedAudio.isEmpty()) {
        	errorMsg.setText("You need at least 1 audio file and at least 1 image.");	
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
		protected Void call() {
			//Add textField for user to select creation name
	    	String fileName = creationName.getText();
	    	// remove ./music part
	    	String musicFile = musicMap.get(musicOptions.getSelectionModel().getSelectedItem());
	    	String audioFileName = "." + fileName + "-audio.wav";
	    	String videoFileName = "." + fileName + "-video.mp4";
	    	String trimmedMusicFileName = "." + fileName + "-music.mp3";
	    	String combinedAudioFileName = "./audiofiles/." + fileName + "-fullaudio.mp3";
	    	
	    	//Create audio based on selected files by concatenating them
	    	List<String> selectedAudioFiles = new ArrayList<String>();
	    	for (String audioFile : selectedAudio) {
	    		selectedAudioFiles.add("." + audioFile + ".wav");
	    	}
	    	Creator.get().combineAudio(selectedAudioFiles, audioFileName);
	    	String time = Creator.get().getTimeOfAudio(audioFileName);
	    	time = time.substring(0, time.length() - 1);
	    	
	    	//Trim audio
	    	Creator.get().trimMusic(musicFile, trimmedMusicFileName, time);
	    	
	    	//Overlay background music on speech
	    	if (musicOptions.getSelectionModel().getSelectedItem() == null) {
	    		combinedAudioFileName = "./audiofiles/." + audioFileName;
	    	} else {
	    		Creator.get().overlayMusic(trimmedMusicFileName, audioFileName, combinedAudioFileName);
	    	}
	    	
	        //Create video based on selected images using the time of the audio file
	    	List<String> selectedImageFiles = new ArrayList<String>();
	    	for (ImageView imageView : selectedImages) {
	    		selectedImageFiles.add(imageMap.get(imageView));
	    	}
	    	Creator.get().makeSlideshow(selectedImageFiles, videoFileName, time);
	    	
	    	//Combine audio and video files to one creation
	    	Creator.get().combine("./images/" + videoFileName, combinedAudioFileName, fileName + ".mp4");

	    	
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
