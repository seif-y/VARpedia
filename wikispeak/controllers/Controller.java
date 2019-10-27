package wikispeak.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Controller {
	
	protected static String themeFileName;

    protected void switchScenes(Pane pane, String FXMLFileName) {
    	onSwitchScenes();
    	
        Stage stage = (Stage) pane.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/" + FXMLFileName));
        try {
            Pane newPage = loader.load();
            Scene scene = new Scene(newPage);
            scene.getStylesheets().clear();
            scene.getStylesheets().add("wikispeak/style/" + themeFileName);
            scene.getStylesheets().add("wikispeak/style/style.css");
            
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	protected void onSwitchScenes() {}
	
	public static void setTheme(String theme) {
		themeFileName = theme;
	}

}
