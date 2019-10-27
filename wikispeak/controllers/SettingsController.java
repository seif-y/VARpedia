package wikispeak.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Creator;
import wikispeak.creation.Creation;
import wikispeak.creation.RatingComparator;
import wikispeak.creation.ViewsComparator;

public class SettingsController extends Controller {

    @FXML
    private AnchorPane pane;

    
    /**
     * Loads "Settings" page
     */
    @FXML
    private void handleBack() {
        switchScenes(pane,"HomePage.fxml");
    }
}
