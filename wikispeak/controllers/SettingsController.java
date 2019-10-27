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
    
    private void initialize() {
    	ObservableList<String> themes = FXCollections.observableArrayList();
        
        themes.addAll("Default", "Yellow");
        themeOptions.setItems(themes);
        
        themeMap.put("Default", "default.css");
        themeMap.put("Yellow", "yellow.css");
    }
    
    

    
    /**
     * Loads "Settings" page
     */
    @FXML
    private void handleBack() {
        switchScenes(pane,"HomePage.fxml");
    }
}
