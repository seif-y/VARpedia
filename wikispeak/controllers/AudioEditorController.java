package wikispeak.controllers;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Creator;
import wikispeak.ImageHandler;
import wikispeak.Wikit;

public class AudioEditorController extends Controller {
    
    @FXML
    private AnchorPane pane;
    @FXML
    private TextArea wikitText;
    @FXML
    private TextArea selectedText;
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
        
        voiceOptions.setStyle("-fx-font: 20px \"System\";");
        errorMsg.setVisible(false);
        wikitText.setText(Wikit.get().getFormattedArticle());
    }
    
    
    @FXML
    private void handleBack() {
    	switchScenes(pane, "NewCreationPage.fxml");
    }
    
    @FXML
    private void handleScrapCreation() {
    	Creator.get().cleanup();
    	switchScenes(pane, "HomePage.fxml");
    }
    
    
    /**
     * Executes when create button is pressed.
     * Checks if the creation name is valid and clear of invalid characters, and if it is, makes the creation.
     * If creation name is already in use, user is asked if they want to overwrite.
     */
    @FXML
    private void handleCreate() {
        String fileName = nameField.getText();
        int numWords = selectedText.getText().split("\\s+").length;
        
        if (fileName == null || fileName.equals("")) {
            errorMsg.setText("Please enter a valid name for your audio file");
            errorMsg.setVisible(true);
            
        } else if (Bash.hasInvalidChars(fileName, true)) {
        	errorMsg.setText("Audio file name contains invalid character(s)");
        	errorMsg.setVisible(true);
        	
        } else if (numWords > 40) {
        	errorMsg.setText("Cannot select a chunk of text greater than 40 words!");
        	errorMsg.setVisible(true);
        		
        } else if (!(new File("./creations/audiofiles/." + fileName + ".wav").exists())) {
        	generateAudio();
        			
        } else {
        	Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Overwrite Warning");
            saveAlert.setHeaderText("The recording " + fileName + " already exists!");
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
    	String selection = selectedText.getText();
    	int numWords = selection.split("\\s+").length;
    	
    	if (numWords > 40) {
    		errorMsg.setText("Cannot select a chunk of text greater than 40 words!");
    		errorMsg.setVisible(true);
    	} else {
    		errorMsg.setText("Playing audio...");
    		errorMsg.setVisible(true);
    		Thread playerThread = new Thread(new PreviewAudio());
    		playerThread.start();
    	}
    }
    
    
    
    @FXML
    private void handleAddText() {
    	selectedText.appendText("\n\n" + wikitText.getSelectedText());
    }
    
    
    
    /**
     * Method to retrieve images from Flickr in a new thread, then load the FinishCreations page
     */
    @FXML
    private void handleNext() {
    	if (new File("./creations/audiofiles").list().length == 0) {
    		errorMsg.setVisible(true);
    		errorMsg.setText("You need to save an audio file to move on.");
    	} else {
    		errorMsg.setVisible(true);
    		errorMsg.setText("Moving on to next stage...");
    		retrieveImages();
    	}
    }
 
    
    /**
     * Method to generate the audio in a new thread
     */
    private void generateAudio() {
    	errorMsg.setVisible(true);
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
     * Task subclass to handle previewing of audio.
     */
    private class PreviewAudio extends Task<Void> {

    	private int _exitCode;
		@Override
		protected Void call() throws Exception {
			
			String voice = voiceMap.get(voiceOptions.getSelectionModel().getSelectedItem());
			String selection = selectedText.getText();
			
			if (voice == null) {
				_exitCode = -1;
				return null;
			}
			
			try {
				Bash.execute("./creations/voices", "echo '(voice_" + voice + ")' > voice.scm").waitFor();
				Bash.execute("./creations/voices", "echo '(SayText \"" + selection + "\")' >> voice.scm").waitFor();
				_exitCode = Bash.execute("./creations/voices", "festival -b voice.scm").waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		return null;
		}
		
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
				if (_exitCode == 0) {
					errorMsg.setText("Finished previewing audio.");
				} else if (_exitCode == -1) {
					errorMsg.setText("Please select a voice before previewing audio.");
				} else {
					errorMsg.setText("Error playing audio, ensure your text contains no invalid characters.");
				}
			});
		}
    	
    }
    
    
    /**
     * Subclass of Task to handle making a new creation.
     */
    private class GenerateAudio extends Task<Void> {
    	
    	int _exitCode;
    	
    	/**
    	 * Call method: Creates temp audio and video files and combines them, then deletes the temp files, using Creator class.
    	 */
        @Override
        protected Void call() throws Exception {
        	
        	String name = nameField.getText();
            String selection = selectedText.getText();
            String voice = voiceMap.get(voiceOptions.getSelectionModel().getSelectedItem());
            
            if (voice == null) {
            	_exitCode = -1;
            	return null;
            }
            
            try {
        		_exitCode = Bash.execute("./creations/audiofiles", "echo \"" + selection + "\" | text2wave -o ." + name + ".wav -eval \"(voice_" + voice + ")\"").waitFor();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
            
            return null;

        }

        
        /**
         * Sets text at bottom to communicate that text file has been saved.
         */
        @Override
        protected void done() {
        	if (_exitCode == 0) {
        		errorMsg.setText(nameField.getText() + " has been saved.");
        	} else if (_exitCode == -1) {
        		errorMsg.setText("Please select a voice before saving audio.");
        	} else {
        		errorMsg.setText("Could not save audio, ensure your text has no invalid characters.");
        	}
        }
    }
    
    
    /**
     * Task subclass to handle calls to Flickr API
     */
    private class CallFlickr extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			String searchTerm = Wikit.get().getTerm();
			if (new File("./creations/images").list().length == 0) {
				ImageHandler.get().saveImages(searchTerm, 10);
			}
			
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(() -> {
				switchScenes(pane, "FinishCreation.fxml");
			});
		}
    	
    }
    
}
