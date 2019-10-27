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
    
    @FXML
    private ComboBox<String> themeOptions;
    private Map<String, String> themeMap = new IdentityHashMap<>();
    
    @FXML
    private void initialize() {
    	ObservableList<String> themes = FXCollections.observableArrayList();
        
        themes.addAll("Default", "Yellow", "Dark");
        themeOptions.setItems(themes);
        
        themeMap.put("Default", "default.css");
        themeMap.put("Yellow", "yellow.css");
        themeMap.put("Dark", "dark.css");
    }
    
    private void changeTheme() {
    	if (themeOptions.getSelectionModel().getSelectedItem() != null) {
    		themeFileName = themeMap.get(themeOptions.getSelectionModel().getSelectedItem());
    	} 	
    }

    
    /**
     * Loads "Home" page
     */
    @FXML
    private void handleBack() {
    	changeTheme();
        switchScenes(pane,"HomePage.fxml");
    }
    
    /**
     * Loads new theme.
     */
    @FXML
    private void handlePreview() {
    	changeTheme();
        switchScenes(pane,"SettingsPage.fxml");
    }
}
