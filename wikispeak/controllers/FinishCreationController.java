package wikispeak.controllers;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Creator;
import wikispeak.Wikit;

public class FinishCreationController {

    private int _numSentences = 10;
    
    @FXML
    private AnchorPane finishCreationPage;
    @FXML
    private TextArea wikitText;
    @FXML
    private Slider slider;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField nameField;
    @FXML
    private Text errorMsg;

    @FXML
    private void initialize() {

        errorMsg.setVisible(false);
        progressBar.setVisible(false);
        
        wikitText.setText(Wikit.get().getFormattedArticle());
        
        slider.setMin(1);
        slider.setMax(Wikit.get().getNumSentences());
    }

    private void updateBarAndMessage(double progress, String message) {
        progressBar.setProgress(progress);
        errorMsg.setText(message);
    }

    @FXML
    private void handleCreate() {
        String creationName = nameField.getText();
        if (creationName == null || creationName.equals("")) {
            errorMsg.setText("Please enter a valid name for your creation");
            errorMsg.setVisible(true);
        } else if (creationName.contains(" ")) {
        	errorMsg.setText("Creation name cannot include a space");
        	errorMsg.setVisible(true);
        } else {
        	
            /*progressBar.setVisible(true);
            errorMsg.setVisible(true);
            Thread creatorThread = new Thread(create);
            creatorThread.start();*/
        	
        	if (!(new File("./creations/" + creationName + ".mp4").exists())) {
        		makeNewCreation();
        	} else {
        		Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
                saveAlert.setTitle("Creation Overwrite Warning");
                saveAlert.setHeaderText(creationName + " already exists!");
                saveAlert.setContentText("If you press \"OK\" you will overwrite this file.");
                saveAlert.showAndWait().ifPresent(response -> {
                	if (response == ButtonType.OK) {
                    	makeNewCreation();
                	}
                });
        	}
        }
    }
    
    private void makeNewCreation() {
    	progressBar.setVisible(true);
        errorMsg.setVisible(true);
        Thread creatorThread = new Thread(create);
        creatorThread.start();
    }

    private Task<Void> create = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
        	
        	String audio = "audioTemp";
        	String video = "videoTemp";
        	String creationName = nameField.getText();

            Platform.runLater(() -> { updateBarAndMessage(0.1, "Generating audio..."); });
            String article = Wikit.get().getArticle((int) slider.getValue());
            Creator.get().makeAudio(article, audio);

            Platform.runLater(() -> { updateBarAndMessage(0.4, "Generating video..."); });
            Creator.get().makeVideo(Wikit.get().getTerm(), video);

            Platform.runLater(() -> { updateBarAndMessage(0.7, "Combining audio with video..."); });
            Creator.get().combine(video, audio, creationName);

            Platform.runLater(() -> { updateBarAndMessage(1, "Creation complete! Redirecting to creations page..."); });
            Creator.get().cleanup(video, audio);

            return null;

        }

        @Override
        protected void done() {
            Platform.runLater(() -> {
            	FXMLLoader loader = new FXMLLoader();
                loader.setLocation(this.getClass().getResource("/wikispeak/resources/CreationsPage.fxml"));
                try {
                    AnchorPane viewCreationPage = loader.load();
                    finishCreationPage.getChildren().clear();
                    finishCreationPage.getChildren().add(viewCreationPage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }); 
        }
    };
}
