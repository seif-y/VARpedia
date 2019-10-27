package wikispeak;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wikispeak.controllers.Controller;

import java.io.IOException;

public class WikiSpeakApp extends Application {
	
	/**
	 * Start method: Loads the home page on the stage
	 */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/HomePage.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("wikispeak/style/style.css");
        scene.getStylesheets().add("wikispeak/style/default.css");
        Controller.setTheme("default.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("VARpedia");
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION);
                quitAlert.setTitle("Exit WikiSpeak");
                quitAlert.setHeaderText("Are you sure you want to quit?");
                quitAlert.setContentText("Any unsaved progress will be lost.");
                quitAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        Platform.exit();
                        System.exit(0);
                    } else {
                    	event.consume();
                    }
                });
            }
        });
    }

    
    /**
     * Main method: launches GUI
     */
    public static void main(String[] args) {
        launch(args);
    }
}
