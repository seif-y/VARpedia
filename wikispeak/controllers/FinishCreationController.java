package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import wikispeak.Creator;
import wikispeak.Wikit;

public class FinishCreationController {

    private int _numSentences = 10;

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
        //TODO: set text for wikitText to search result (formatted)
        wikitText.setText(Wikit.get().getArticle());
        //TODO: set max value for slider to number of sentences in creation
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
        } else {
            progressBar.setVisible(true);
            errorMsg.setVisible(true);
            Thread creatorThread = new Thread(create);
            creatorThread.start();
        }
    }

    private Task<Void> create = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
        	
        	String audio = "audioTemp";
        	String video = "videoTemp";
        	String creationName = nameField.getText();

            Platform.runLater(() -> { updateBarAndMessage(0.1, "Generating audio..."); });
            //TODO: generate audio
            String article = Wikit.get().getArticle((int) slider.getValue());
            System.out.println(article);
            Creator.get().makeAudio(article, audio);
            System.out.println("finished audio method");

            Platform.runLater(() -> { updateBarAndMessage(0.4, "Generating video..."); });
            //TODO: generate video
            Creator.get().makeVideo(Wikit.get().getTerm(), video);

            Platform.runLater(() -> { updateBarAndMessage(0.7, "Combining audio with video..."); });
            //TODO: combine audio and video
            Creator.get().combine(video, audio, creationName);

            Platform.runLater(() -> { updateBarAndMessage(1, "Creation complete! Redirecting to creations page..."); });

            //TODO: save creation onto combyooter (have to consider where creations folder will be)

            return null;

        }

        @Override
        protected void done() {
            //TODO: load "view creations" or "home" page
        }
    };
}
