package wikispeak.controllers;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Creator;
import wikispeak.ImageHandler;
import wikispeak.Wikit;

public class AudioEditorController {
    
    @FXML
    private AnchorPane finishCreationPage;
    @FXML
    private TextArea wikitText;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> voiceOptions;
    @FXML
    private Text errorMsg;
    
    private Map<String, String> voiceMap = new IdentityHashMap<>();

    
    /**
     * Initialize method: Sets progress bar and error message to invisible, sets text area to display wikit article,
     * and sets max value of slider to number of sentences in article.
     */
    @FXML
    private void initialize() {
    	
    	if (!(new File("./voices")).exists()) {
    		Process process = Bash.execute(".", "mkdir ./creations/voices");
    		try {
    			process.waitFor();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	
        ObservableList<String> voices = FXCollections.observableArrayList();
        voices.addAll("Default", "Auckland");
        voiceOptions.setItems(voices);
        
        voiceMap.put("Default", "kal_diphone");
        voiceMap.put("Auckland", "akl_nz_jdt_diphone");
        
        errorMsg.setVisible(false);
        wikitText.setText(Wikit.get().getFormattedArticle());
    }
    
    
    /**
     * Executes when create button is pressed.
     * Checks if the creation name is valid and clear of invalid characters, and if it is, makes the creation.
     * If creation name is already in use, user is asked if they want to overwrite.
     */
    @FXML
    private void handleCreate() {
        String creationName = nameField.getText();
        
        if (creationName == null || creationName.equals("")) {
            errorMsg.setText("Please enter a valid name for your audio file");
            errorMsg.setVisible(true);
            
        } else if (Bash.hasInvalidChars(creationName, true)) {
        	errorMsg.setText("Audio file name contains invalid character(s)");
        	errorMsg.setVisible(true);
        		
        } else if (!(new File("./creations/audiofiles" + creationName + ".mp4").exists())) {
        	generateAudio();
        			
        } else {
        	Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Overwrite Warning");
            saveAlert.setHeaderText(creationName + " already exists!");
            saveAlert.setContentText("If you press \"OK\" you will overwrite this file.");
            saveAlert.showAndWait().ifPresent(response -> {
            	if (response == ButtonType.OK) {
            		generateAudio();
            	}
            });
        }
    }

    
    /**
     * Executed when preview button is pressed. Plays the selected audio from the text area using the selected voice.
     */
    
    @FXML
    private void handlePreview() {
    	String selection = wikitText.getSelectedText();
    	int numWords = selection.split("\\s+").length;
    	String voice = voiceMap.get(voiceOptions.getSelectionModel().getSelectedItem());
    	
    	if (numWords > 40) {
    		Alert wordAlert = new Alert(Alert.AlertType.ERROR);
    		wordAlert.setTitle("Too many words!");
    		wordAlert.setHeaderText("Your selection may contain no more than 40 words.");
    		wordAlert.setContentText("Please select a smaller chunk of text.");
    		wordAlert.showAndWait();
    	} else {
    		Bash.execute("./creations/voices", "echo '(voice_" + voice + ")' > voice.scm");
    		Bash.execute("./creations/voices", "echo '(SayText \"" + selection + "\")' >> voice.scm");
    		Bash.execute("./creations/voices", "festival -b voice.scm");
    	}
    }
    
    /**
     * Method to retrieve images from Flickr in a new thread, then load the FinishCreations page
     */
    @FXML
    private void handleNext() {
    	
    	errorMsg.setText("Moving on to next stage...");
    	retrieveImages();
    }
 
    
    /**
     * Method to generate the audio in a new thread
     */
    private void generateAudio() {
    	errorMsg.setText("Generating Audio...");
        Thread creatorThread = new Thread(new GenerateAudio());
        creatorThread.start();
    }

    
    /**
     * Method to retrieve images in a new thread
     */
    private void retrieveImages() {
    	Thread imageThread = new Thread(new CallFlickr());
    	imageThread.start();
    }
    
    
    /**
     * Subclass of Task to handle making a new creation.
     */
    private class GenerateAudio extends Task<Void> {
    	
    	/**
    	 * Call method: Creates temp audio and video files and combines them, then deletes the temp files, using Creator class.
    	 */
        @Override
        protected Void call() throws Exception {
        	
        	String name = nameField.getText();
            String selection = wikitText.getSelectedText();
            String voice = voiceMap.get(voiceOptions.getSelectionModel().getSelectedItem());
            
            Creator.get().makeAudio(selection, name, voice);
            
            return null;

        }

        
        /**
         * Sets text at bottom to communicate that text file has been saved.
         */
        @Override
        protected void done() {
            errorMsg.setText(nameField.getText() + " has been saved.");
            errorMsg.setVisible(true);
        }
    }
    
    
    /**
     * Task subclass to handle calls to Flickr API
     */
    private class CallFlickr extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			String searchTerm = Wikit.get().getTerm();
			ImageHandler.get().saveImages(searchTerm, 10);
			
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
				Pane parent = (Pane) finishCreationPage.getParent();
		    	FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(this.getClass().getResource("/wikispeak/resources/FinishCreation.fxml"));
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
