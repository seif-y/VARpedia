package wikispeak.controllers;

import javafx.fxml.FXML;
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
}
