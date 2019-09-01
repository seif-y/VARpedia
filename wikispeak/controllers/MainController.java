package wikispeak.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane display;

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

    @FXML
    private void initialize() { handleHomeButton(); }

    @FXML
    private void handleHomeButton() { updateDisplay("/wikispeak/resources/HomePage.fxml"); }

    @FXML
    private void handleNewCreation() { updateDisplay("/wikispeak/resources/NewCreationPage.fxml"); }

    @FXML
    private void handleViewCreations() { updateDisplay("/wikispeak/resources/CreationsPage.fxml"); }

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
