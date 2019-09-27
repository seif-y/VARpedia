package wikispeak.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane display;
    
    
    /**
     * Updates the inner anchor pane to display the .fxml file specified in the parameter
     * @param fileName The name of the .fxml file to be displayed
     */
    private void updateDisplay(String fileName) {

        display.getChildren().clear();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(fileName)) ;
        try {
            AnchorPane page = loader.load();
            display.getChildren().add(page);
        } catch (IOException e) {
        	e.printStackTrace();
        }

    }

    
    /**
     * Initialize method: loads "Home" page
     */
    @FXML
    private void initialize() { handleHomeButton(); }

    
    /**
     * Loads "Home" page
     */
    @FXML
    private void handleHomeButton() { updateDisplay("/wikispeak/resources/HomePage.fxml"); }

    
    /**
     * Loads "New Creation" page
     */
    @FXML
    private void handleNewCreation() { updateDisplay("/wikispeak/resources/NewCreationPage.fxml"); }

    
    /**
     * Loads "View Creations" page
     */
    @FXML
    private void handleViewCreations() { updateDisplay("/wikispeak/resources/CreationsPage.fxml"); }

    
    /**
     * Creates an alert to ask user to confirm if they want to close the window
     */
    @FXML
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
