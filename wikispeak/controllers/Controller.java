package wikispeak.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public abstract class Controller {
	
	protected void switchScenes(Pane pane, String FXMLFileName) {
		
		pane.getChildren().clear();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/" + FXMLFileName));
        try {
            AnchorPane newPage = loader.load();
            pane.getChildren().clear();
            pane.getChildren().add(newPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        onSwitchScenes();
	}
	
	protected void onSwitchScenes() {}

}
