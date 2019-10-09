package wikispeak.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Controller {

    protected void switchScenes(Pane pane, String FXMLFileName) {
        Stage stage = (Stage) pane.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/" + FXMLFileName));
        try {
            Pane newPage = loader.load();
            stage.setScene(new Scene(newPage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	protected void onSwitchScenes() {}

}
