package wikispeak.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewCreationsController {

    private List<String> _creations = new ArrayList<String>();
    private boolean _creationSelected;


    @FXML
    private ListView<String> creationList;

    @FXML
    private Text creationDisplay;

    @FXML
    private void initialize() {

        _creationSelected = false;

        _creations.add("lick");
        _creations.add("my");
        _creations.add("saggy");
        _creations.add("balls");

        creationList.getItems().addAll(_creations);
        creationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void handleSelectCreation() {
        if (!_creationSelected) { _creationSelected = true; }


        try {
        	String creationName = creationList.getSelectionModel().getSelectedItem().toString();
        	creationDisplay.setText(creationName);
        } catch (NullPointerException e) {}

        
    }

    @FXML
    private void handleDelete() {
        if (_creationSelected) {
            String creationName = creationDisplay.getText();
            Alert delAlert = new Alert(Alert.AlertType.WARNING);
            delAlert.setTitle("Delete Creation");
            delAlert.setHeaderText("Are you sure?");
            delAlert.setContentText("You are about to delete " + creationName);
            delAlert.showAndWait();
        }
    }

    @FXML
    private void handlePlay() {
        if (_creationSelected) {
            Thread playerThread = new Thread(playCreation);
            playerThread.start();
        }
    }

    Task<Void> playCreation = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
            //TODO: ffplay video
            return null;
        }
    };
}
