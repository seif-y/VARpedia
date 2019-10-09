package wikispeak.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

public class MainController extends Controller {

    @FXML
    private AnchorPane pane;
    
    /**
     * Loads "New Creation" page
     */
    @FXML
    private void handleNewCreation() {
        switchScenes(pane,"NewCreationPage.fxml");
    }

    
    /**
     * Loads "View Creations" page
     */
    @FXML
    private void handleViewCreations() {
        switchScenes(pane,"CreationsPage.fxml");
    }

    
    /**
     * Creates an alert to ask user to confirm if they want to close the window
     */
    private void handleQuit() {
        Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        quitAlert.setTitle("Exit WikiSpeak");
        quitAlert.setHeaderText("Are you sure you want to quit?");
        quitAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
