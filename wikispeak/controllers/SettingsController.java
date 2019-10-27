package wikispeak.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

public class SettingsController extends Controller {

    @FXML
    private AnchorPane pane;
    
    
    /**
     * Loads "Home" page
     */
    @FXML
    private void handleBack() {
        switchScenes(pane,"HomePage.fxml");
    }
    
    
    /**
     * Loads default theme.
     */
    @FXML
    private void handleDefaultTheme() {
    	themeFileName = "default.css";
        switchScenes(pane,"SettingsPage.fxml");
    }
    
    /**
     * Loads default theme.
     */
    @FXML
    private void handleDarkTheme() {
    	themeFileName = "dark.css";
        switchScenes(pane,"SettingsPage.fxml");
    }
    
    /**
     * Loads default theme.
     */
    @FXML
    private void handleMustardTheme() {
    	themeFileName = "yellow.css";
        switchScenes(pane,"SettingsPage.fxml");
    }
    
    /**
     * Loads default theme.
     */
    @FXML
    private void handlePastelTheme() {
    	themeFileName = "pastel.css";
        switchScenes(pane,"SettingsPage.fxml");
    }
    
    /**
     * Loads default theme.
     */
    @FXML
    private void handlePrimaryTheme() {
    	themeFileName = "pastel.css";
        switchScenes(pane,"SettingsPage.fxml");
    }
}
